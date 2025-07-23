package com.regisx001.validationsystem.services.impl;

import java.util.List;
import java.util.UUID;

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
import com.regisx001.validationsystem.repositories.ArticleRepository;
import com.regisx001.validationsystem.services.ApproveAIService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class ApproveAIServiceImpl implements ApproveAIService {

    private final ArticleRepository articleRepository;
    private final AIAnalysisConfig config;
    private final ChatClient chatClient;
    private final ObjectMapper objectMapper;

    @Value("${spring.ai.openai.chat.options.model}")
    private String model;

    @Override
    public ApprovalResult analyseArticle(UUID id) {
        long startTime = System.currentTimeMillis();
        Article article = articleRepository.findById(id).orElseThrow(() -> new RuntimeException("Article Not found"));

        if (!isValidForAnalysis(article)) {
            throw new RuntimeException("Article doesn't meet basic requirements by system-analysis");
        }

        String prompt = buildAnalysisPrompt(article);

        String aiResponse = chatClient.prompt(prompt).call().content();
        long endTime = System.currentTimeMillis();
        Integer analyzeTimeMs = (int) (endTime - startTime);
        log.info("AI analysis time: {} ms", analyzeTimeMs);

        AIAnalysisResponse analysisResponse = parseAIResponse(aiResponse);
        return buildApprovalResult(article, analysisResponse, analyzeTimeMs);
    }

    @Override
    public String getUsedLLM() {
        return model;
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

    private ApprovalResult buildApprovalResult(Article article, AIAnalysisResponse response, Integer processingTimeMs) {
        ApprovalDecision decision = determineDecision(response);

        // Get feedback from root level or combine nested feedback
        String aiAnalysis = response.getFeedback();
        if (aiAnalysis == null || aiAnalysis.trim().isEmpty()) {
            StringBuilder combinedFeedback = new StringBuilder();
            if (response.getContentQuality() != null && response.getContentQuality().getFeedback() != null) {
                combinedFeedback.append("Content Quality: ").append(response.getContentQuality().getFeedback())
                        .append(". ");
            }
            if (response.getGrammar() != null && response.getGrammar().getFeedback() != null) {
                combinedFeedback.append("Grammar: ").append(response.getGrammar().getFeedback()).append(". ");
            }
            if (response.getAppropriateness() != null && response.getAppropriateness().getFeedback() != null) {
                combinedFeedback.append("Appropriateness: ").append(response.getAppropriateness().getFeedback())
                        .append(".");
            }
            aiAnalysis = combinedFeedback.toString().trim();
        }

        // Convert recommendations list to string
        String recommendationsStr = "";
        if (response.getRecommendations() != null && !response.getRecommendations().isEmpty()) {
            recommendationsStr = String.join("; ", response.getRecommendations());
        }

        return ApprovalResult.builder()
                .articleId(article.getId())
                .decision(decision)
                .confidenceScore(response.getOverallScore())
                .aiAnalysis(aiAnalysis)
                .recommendations(recommendationsStr)
                .readabilityScore(response.getContentQuality() != null ? response.getContentQuality().getScore() : null)
                .grammarScore(response.getGrammar() != null ? response.getGrammar().getScore() : null)
                .seoScore(response.getSeo() != null ? response.getSeo().getScore() : null)
                .aiModel(model)
                .processingTimeMs(processingTimeMs)
                .build();
    }

    private AIAnalysisResponse parseAIResponse(String response) {
        try {
            // Simple JSON parsing - if it fails, create a basic response
            log.debug("Attempting to parse AI response: {}", response);
            return objectMapper.readValue(response, AIAnalysisResponse.class);
        } catch (Exception e) {
            log.error("Failed to parse AI response. Error: {}, Response: {}", e.getMessage(), response, e);
            return createFallbackResponse(response);
        }
    }

    private AIAnalysisResponse createFallbackResponse(String rawResponse) {
        return AIAnalysisResponse.builder()
                .overallScore(0.5) // Neutral score
                .recommendation("NEEDS_MANUAL_REVIEW")
                .feedback("AI analysis completed but requires manual review. Raw response: " + rawResponse)
                .recommendations(List.of("Manual review required"))
                .build();
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
}
