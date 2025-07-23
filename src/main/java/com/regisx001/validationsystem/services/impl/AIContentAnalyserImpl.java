package com.regisx001.validationsystem.services.impl;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.regisx001.validationsystem.config.AIAnalysisConfig;
import com.regisx001.validationsystem.config.AIPromptTemplates;
import com.regisx001.validationsystem.domain.dtos.AIAnalysisResponse;
import com.regisx001.validationsystem.domain.entities.ApprovalResult;
import com.regisx001.validationsystem.domain.entities.Article;
import com.regisx001.validationsystem.domain.enums.ApprovalDecision;
import com.regisx001.validationsystem.services.AIContentAnalyser;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@RequiredArgsConstructor
@Service
public class AIContentAnalyserImpl implements AIContentAnalyser {

    private final ChatClient chatClient;
    private final ObjectMapper objectMapper;
    private final AIAnalysisConfig config;

    @Value("${spring.ai.openai.chat.options.model}")
    private final static String model = "gpt-4";

    private static final Pattern JSON_CODE_BLOCK_PATTERN = Pattern.compile("``````");
    private static final Pattern JSON_OBJECT_PATTERN = Pattern.compile("\\{[\\s\\S]*\\}");

    @Override
    public ApprovalResult analyzeContent(Article article) {
        log.info("Starting AI analysis for article: {}", article.getId());

        try {
            // Basic validation first
            if (!isValidForAnalysis(article)) {
                return createRejectionResult(article, "Article doesn't meet basic requirements");
            }

            // Call AI for analysis
            String prompt = buildAnalysisPrompt(article);
            String aiResponse = chatClient.prompt(prompt).call().content();

            String json = extractJSON(aiResponse);
            // Parse AI response
            AIAnalysisResponse analysisResponse = parseAIResponse(json);

            // Convert to ApprovalResult
            return buildApprovalResult(article, analysisResponse);

        } catch (Exception e) {
            log.error("Error during AI analysis for article {}: {}", article.getId(), e.getMessage());
            return createErrorResult(article, e.getMessage());
        }

    }

    private String extractJSON(String response) {
        if (response == null || response.trim().isEmpty()) {
            return null;
        }

        // Method 1: Try to extract from code blocks (``````)
        Matcher codeBlockMatcher = JSON_CODE_BLOCK_PATTERN.matcher(response);
        if (codeBlockMatcher.find()) {
            String extracted = codeBlockMatcher.group(1).trim();
            log.debug("Extracted JSON from code block: {}", extracted);
            return extracted;
        }

        // Method 2: Try to find JSON object pattern { ... }
        Matcher jsonMatcher = JSON_OBJECT_PATTERN.matcher(response);
        if (jsonMatcher.find()) {
            String extracted = jsonMatcher.group().trim();
            log.debug("Extracted JSON object: {}", extracted);
            return extracted;
        }

        // Method 3: Check if the entire response is JSON
        String trimmed = response.trim();
        if (trimmed.startsWith("{") && trimmed.endsWith("}")) {
            log.debug("Entire response appears to be JSON");
            return trimmed;
        }

        log.warn("No JSON pattern found in response");
        return null;
    }

    private boolean isValidForAnalysis(Article article) {
        int wordCount = article.getContent().split("\\s+").length;
        return wordCount >= config.getMinWordCount() &&
                wordCount <= config.getMaxWordCount() &&
                article.getTitle() != null && !article.getTitle().trim().isEmpty();
    }

    private String buildAnalysisPrompt(Article article) {
        return AIPromptTemplates.CONTENT_ANALYSIS_PROMPT
                .replace("{title}", article.getTitle())
                .replace("{content}", article.getContent());
    }

    private AIAnalysisResponse parseAIResponse(String response) {
        try {
            // Simple JSON parsing - if it fails, create a basic response
            return objectMapper.readValue(response, AIAnalysisResponse.class);
        } catch (Exception e) {
            log.warn("Failed to parse AI response, creating fallback response");
            return createFallbackResponse(response);
        }
    }

    private AIAnalysisResponse createFallbackResponse(String rawResponse) {
        return AIAnalysisResponse.builder()
                .overallScore(0.5) // Neutral score
                .recommendation("NEEDS_MANUAL_REVIEW")
                .feedback("AI analysis completed but requires manual review. Raw response: " + rawResponse)
                .build();
    }

    private ApprovalResult buildApprovalResult(Article article, AIAnalysisResponse response) {
        ApprovalDecision decision = determineDecision(response);

        return ApprovalResult.builder()
                .articleId(article.getId())
                .decision(decision)
                .confidenceScore(response.getOverallScore())
                .aiAnalysis(response.getFeedback())
                .recommendations(response.getRecommendations())
                .readabilityScore(response.getContentQuality() != null ? response.getContentQuality().getScore() : null)
                .grammarScore(response.getGrammar() != null ? response.getGrammar().getScore() : null)
                .seoScore(response.getSeo() != null ? response.getSeo().getScore() : null)
                .aiModel(model)
                .build();
    }

    private ApprovalDecision determineDecision(AIAnalysisResponse response) {
        double score = response.getOverallScore();

        if (score >= config.getAutoApprovalThreshold()) {
            return ApprovalDecision.APPROVED;
        } else if (score <= config.getAutoRejectionThreshold()) {
            return ApprovalDecision.REJECTED;
        } else {
            return ApprovalDecision.REQUIRES_MANUAL_REVIEW;
        }
    }

    private ApprovalResult createRejectionResult(Article article, String reason) {
        return ApprovalResult.builder()
                .articleId(article.getId())
                .decision(ApprovalDecision.REJECTED)
                .confidenceScore(0.0)
                .aiAnalysis(reason)
                .aiModel("system-validation")
                .build();
    }

    private ApprovalResult createErrorResult(Article article, String error) {
        return ApprovalResult.builder()
                .articleId(article.getId())
                .decision(ApprovalDecision.REQUIRES_MANUAL_REVIEW)
                .confidenceScore(0.0)
                .aiAnalysis("Error during analysis: " + error)
                .aiModel("error-handler")
                .build();
    }
}
