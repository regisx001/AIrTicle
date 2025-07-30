package com.regisx001.validationsystem.controller;

import com.regisx001.validationsystem.domain.entities.Article;
import com.regisx001.validationsystem.repositories.AnalyseHistoryRepository;
import com.regisx001.validationsystem.services.AIAnalyseService;
import com.regisx001.validationsystem.services.ArticleService;

import lombok.RequiredArgsConstructor;

import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/articles")

@RequiredArgsConstructor
public class ArticleApprovalController {

    // private final ArticleRepository articleRepository;
    private final ArticleService articleService;
    private final AIAnalyseService aiAnalyseService;
    private final AnalyseHistoryRepository analyseHistoryRepository;

    @GetMapping
    public ResponseEntity<?> getAllArticles(Pageable pageable) {
        return ResponseEntity.ok(articleService.getAllArticles(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getArticle(@PathVariable UUID id) {
        return ResponseEntity.ok(articleService.getArticleById(id));
    }

    @GetMapping("/{id}/history")
    public ResponseEntity<?> getArticleAnalyseHistory(@PathVariable UUID id) {
        return ResponseEntity.ok(analyseHistoryRepository.findByArticle_IdOrderByCreatedAtDesc(id));
    }

    @PostMapping
    public ResponseEntity<?> createArticle(@RequestBody Article article) {
        Article ar = articleService.createArticle(article);
        return ResponseEntity.ok(ar);
    }

    @PostMapping("/{id}/review")
    public ResponseEntity<?> submitForReview(@PathVariable UUID id) {
        return ResponseEntity.ok(aiAnalyseService.analyseArticleManual(id));
    }

    // @GetMapping("/review-llm")
    // public ResponseEntity<?> getReviewLLM() {
    // return ResponseEntity.ok(approveAIService.getUsedLLM());
    // }

    @GetMapping("{id}/review")
    public ResponseEntity<?> getArticleLatestReview(@PathVariable UUID id) {
        return ResponseEntity.ok(aiAnalyseService.getLatestApprovalResult(id));
    }

}
