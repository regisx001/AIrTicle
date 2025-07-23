package com.regisx001.validationsystem.services;

import com.regisx001.validationsystem.domain.entities.Article;
import com.regisx001.validationsystem.domain.entities.ApprovalResult;
import com.regisx001.validationsystem.domain.enums.ArticleStatus;
import com.regisx001.validationsystem.domain.enums.ApprovalDecision;
import com.regisx001.validationsystem.repositories.ArticleRepository;
import com.regisx001.validationsystem.repositories.ApprovalResultRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ArticleApprovalService {

    private final ArticleRepository articleRepository;
    private final ApprovalResultRepository approvalResultRepository;
    private final AIContentAnalyser aiContentAnalyser;

    @Transactional
    public Article submitForApproval(UUID articleId) {
        Article article = getArticleById(articleId);

        // Simple validation
        // if (article.getStatus() != ArticleStatus.DRAFT) {
        // throw new IllegalStateException("Article must be in DRAFT status to submit
        // for approval");
        // }

        // Update status
        article.setStatus(ArticleStatus.SUBMITTED_FOR_APPROVAL);
        article = articleRepository.save(article);

        log.info("Article {} submitted for approval", articleId);

        // Start AI analysis
        processAIAnalysis(article);

        return article;
    }

    private void processAIAnalysis(Article article) {
        try {
            // Update status to under review
            article.setStatus(ArticleStatus.UNDER_AI_REVIEW);
            articleRepository.save(article);

            // Run AI analysis
            ApprovalResult result = aiContentAnalyser.analyzeContent(article);

            // Save result
            approvalResultRepository.save(result);

            // Process decision
            processDecision(article, result);

        } catch (Exception e) {
            log.error("AI analysis failed for article {}: {}", article.getId(), e.getMessage());
            // Set to manual review on error
            article.setStatus(ArticleStatus.MANUAL_REVIEW_REQUIRED);
            articleRepository.save(article);
        }
    }

    private void processDecision(Article article, ApprovalResult result) {
        switch (result.getDecision()) {
            case APPROVED:
                autoApprove(article, result);
                break;
            case REJECTED:
                autoReject(article, result);
                break;
            case REQUIRES_MANUAL_REVIEW:
                requireManualReview(article);
                break;
        }
    }

    private void autoApprove(Article article, ApprovalResult result) {
        article.setStatus(ArticleStatus.APPROVED);
        article.setApprovedBy("AI_SYSTEM");
        article.setApprovedAt(LocalDateTime.now());
        article.setFeedback(result.getAiAnalysis());
        articleRepository.save(article);

        log.info("Article {} auto-approved by AI", article.getId());
    }

    private void autoReject(Article article, ApprovalResult result) {
        article.setStatus(ArticleStatus.REJECTED);
        article.setRejectedBy("AI_SYSTEM");
        article.setRejectedAt(LocalDateTime.now());
        article.setFeedback(result.getAiAnalysis());
        articleRepository.save(article);

        log.info("Article {} auto-rejected by AI", article.getId());
    }

    private void requireManualReview(Article article) {
        article.setStatus(ArticleStatus.MANUAL_REVIEW_REQUIRED);
        articleRepository.save(article);

        log.info("Article {} requires manual review", article.getId());
    }

    @Transactional
    public Article manualApprove(UUID articleId, String approvedBy, String feedback) {
        Article article = getArticleById(articleId);

        article.setStatus(ArticleStatus.APPROVED);
        article.setApprovedBy(approvedBy);
        article.setApprovedAt(LocalDateTime.now());
        article.setFeedback(feedback);

        return articleRepository.save(article);
    }

    @Transactional
    public Article manualReject(UUID articleId, String rejectedBy, String feedback) {
        Article article = getArticleById(articleId);

        article.setStatus(ArticleStatus.REJECTED);
        article.setRejectedBy(rejectedBy);
        article.setRejectedAt(LocalDateTime.now());
        article.setFeedback(feedback);

        return articleRepository.save(article);
    }

    public Article getArticleStatus(UUID articleId) {
        return getArticleById(articleId);
    }

    public ApprovalResult getApprovalResult(UUID articleId) {
        return approvalResultRepository.findByArticleId(articleId)
                .orElse(null);
    }

    private Article getArticleById(UUID articleId) {
        return articleRepository.findById(articleId)
                .orElseThrow(() -> new IllegalArgumentException("Article not found: " + articleId));
    }
}
