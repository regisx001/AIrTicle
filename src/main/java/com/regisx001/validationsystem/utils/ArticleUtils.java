package com.regisx001.validationsystem.utils;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.regisx001.validationsystem.config.AIAnalysisConfig;
import com.regisx001.validationsystem.config.AIPromptTemplates;
import com.regisx001.validationsystem.domain.dtos.AIAnalysisResponse;
import com.regisx001.validationsystem.domain.entities.ApprovalResult;
import com.regisx001.validationsystem.domain.entities.Article;
import com.regisx001.validationsystem.domain.enums.ApprovalDecision;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ArticleUtils {

    private final AIAnalysisConfig config;
    private final ObjectMapper objectMapper;

    @Value("${spring.ai.openai.chat.options.model}")
    private String model;

    public boolean isValidForAnalysis(Article article) {
        int wordCount = article.getContent().split("\\s+").length;
        return wordCount >= config.getMinWordCount() &&
                wordCount <= config.getMaxWordCount() &&
                article.getTitle() != null && !article.getTitle().trim().isEmpty();
    }

    public String buildAnalysisPrompt(Article article) {
        return AIPromptTemplates.CONTENT_ANALYSIS_PROMPT
                .replace("{title}", article.getTitle())
                .replace("{content}", article.getContent());
    }

    public AIAnalysisResponse parseAIResponse(String response) {
        try {
            return objectMapper.readValue(response, AIAnalysisResponse.class);
        } catch (Exception e) {
            return AIAnalysisResponse.builder()
                    .overallScore(0.5) // Neutral score
                    .recommendation("NEEDS_MANUAL_REVIEW")
                    .feedback("AI analysis completed but requires manual review. Raw response: " + response)
                    .recommendations(List.of("Manual review required"))
                    .build();
        }
    }

    public ApprovalDecision determineDecision(AIAnalysisResponse response) {
        double score = response.getOverallScore();

        if (score >= config.getAutoApprovalThreshold()) {
            return ApprovalDecision.APPROVED;
        } else if (score <= config.getAutoRejectionThreshold()) {
            return ApprovalDecision.REJECTED;
        } else {
            return ApprovalDecision.REQUIRES_MANUAL_REVIEW;
        }
    }

    public ApprovalResult buildApprovalResult(Article article, AIAnalysisResponse response, Integer processingTimeMs) {
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
}
