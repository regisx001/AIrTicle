package com.regisx001.validationsystem.controller;

import com.regisx001.validationsystem.domain.entities.Article;
import com.regisx001.validationsystem.repositories.ArticleRepository;
import com.regisx001.validationsystem.services.ApproveAIService;

import lombok.RequiredArgsConstructor;

import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/articles")
@RequiredArgsConstructor
public class ArticleApprovalController {

    private final ArticleRepository articleRepository;
    private final ApproveAIService approveAIService;

    @GetMapping
    public ResponseEntity<?> getAllArticles(Pageable pageable) {
        return ResponseEntity.ok(articleRepository.findAll(pageable));
    }

    @PostMapping
    public ResponseEntity<Article> createArticle(@RequestBody Article article) {
        return ResponseEntity.ok(articleRepository.save(article));
    }

    @PostMapping("/{id}/review")
    public ResponseEntity<?> submitForReview(@PathVariable UUID id) {
        return ResponseEntity.ok(approveAIService.analyseArticle(id));
    }

    @GetMapping("/review-llm")
    public ResponseEntity<?> getReviewLLM() {
        return ResponseEntity.ok(approveAIService.getUsedLLM());
    }

}
