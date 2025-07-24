package com.regisx001.validationsystem.controller;

import com.regisx001.validationsystem.domain.entities.Article;
import com.regisx001.validationsystem.services.ArticleService;
import com.regisx001.validationsystem.services.legacy.ApproveAIService;

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
    private final ApproveAIService approveAIService;

    @GetMapping
    public ResponseEntity<?> getAllArticles(Pageable pageable) {
        return ResponseEntity.ok(articleService.getAllArticles(pageable));
    }

    @PostMapping
    public ResponseEntity<?> createArticle(@RequestBody Article article) {
        Article ar = articleService.createArticle(article);
        return ResponseEntity.ok(ar);
    }

    @PostMapping("/{id}/review")
    public ResponseEntity<?> submitForReview(@PathVariable UUID id) {
        return ResponseEntity.ok(approveAIService.analyseArticle(id));
    }

    @GetMapping("/review-llm")
    public ResponseEntity<?> getReviewLLM() {
        return ResponseEntity.ok(approveAIService.getUsedLLM());
    }

    @GetMapping("/review")
    public ResponseEntity<?> getArticleLatestReview() {
        return ResponseEntity.ok(approveAIService.getUsedLLM());
    }

}
