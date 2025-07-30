package com.regisx001.validationsystem.services.impl;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.regisx001.validationsystem.domain.entities.Article;
import com.regisx001.validationsystem.repositories.ArticleRepository;
import com.regisx001.validationsystem.services.AIAnalyseService;
import com.regisx001.validationsystem.services.ArticleService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ArticleServiceImpl implements ArticleService {

    private final ArticleRepository articleRepository;
    private final AIAnalyseService aiAnalyseService;

    @Override
    public Article createArticle(Article article) {

        if (article.getTitle() == null || article.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("Article title cannot be null or empty");
        }

        if (article.getContent() == null || article.getContent().trim().isEmpty()) {
            throw new IllegalArgumentException("Article content cannot be null or empty");
        }

        Article savedArticle = articleRepository.save(article);

        aiAnalyseService.analyseArticle(savedArticle.getId());
        return savedArticle;
    }

    @Override
    public Article getArticleById(UUID id) {

        if (id == null) {
            throw new IllegalArgumentException("Article ID cannot be null");
        }

        return articleRepository.findById(id)
                .orElseThrow(() -> {
                    return new IllegalArgumentException("Article not found with ID: " + id);
                });
    }

    @Override
    public Article updateArticle(UUID id, Article article) {

        if (id == null) {
            throw new IllegalArgumentException("Article ID cannot be null");
        }

        Article existingArticle = getArticleById(id);

        // Update fields if they are provided
        if (article.getTitle() != null && !article.getTitle().trim().isEmpty()) {
            existingArticle.setTitle(article.getTitle());
        }

        if (article.getContent() != null && !article.getContent().trim().isEmpty()) {
            existingArticle.setContent(article.getContent());
        }

        if (article.getFeaturedImage() != null) {
            existingArticle.setFeaturedImage(article.getFeaturedImage());
        }

        if (article.getStatus() != null) {
            existingArticle.setStatus(article.getStatus());
        }

        if (article.getIsPublished() != null) {
            existingArticle.setIsPublished(article.getIsPublished());

            // Set publishedAt timestamp when publishing
            if (article.getIsPublished() && existingArticle.getPublishedAt() == null) {
                existingArticle.setPublishedAt(LocalDateTime.now());
            }
        }

        if (article.getFeedback() != null) {
            existingArticle.setFeedback(article.getFeedback());
        }

        if (article.getApprovedBy() != null) {
            existingArticle.setApprovedBy(article.getApprovedBy());
            existingArticle.setApprovedAt(LocalDateTime.now());
        }

        if (article.getRejectedBy() != null) {
            existingArticle.setRejectedBy(article.getRejectedBy());
            existingArticle.setRejectedAt(LocalDateTime.now());
        }

        Article updatedArticle = articleRepository.save(existingArticle);
        return updatedArticle;
    }

    @Override
    public void deleteArticle(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("Article ID cannot be null");
        }

        Article existingArticle = getArticleById(id);

        articleRepository.delete(existingArticle);
    }

    @Override
    public Page<Article> getAllArticles(Pageable pageable) {

        Page<Article> articles = articleRepository.findAll(pageable);
        return articles;
    }

}
