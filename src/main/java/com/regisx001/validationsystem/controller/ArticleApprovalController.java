package com.regisx001.validationsystem.controller;

import com.regisx001.validationsystem.domain.entities.Article;
import com.regisx001.validationsystem.repositories.ArticleRepository;
import com.regisx001.validationsystem.domain.entities.ApprovalResult;
import com.regisx001.validationsystem.services.ArticleApprovalService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/articles")
@RequiredArgsConstructor
public class ArticleApprovalController {

    private final ArticleApprovalService approvalService;
    private final ArticleRepository articleRepository;

    @PostMapping
    public ResponseEntity<Article> createArticle(@RequestBody Article article) {
        return ResponseEntity.ok(articleRepository.save(article));
    }

    @PostMapping("/{id}/submit-approval")
    public ResponseEntity<Article> submitForApproval(@PathVariable UUID id) {
        Article article = approvalService.submitForApproval(id);
        return ResponseEntity.ok(article);
    }

    @GetMapping("/{id}/status")
    public ResponseEntity<Article> getStatus(@PathVariable UUID id) {
        Article article = approvalService.getArticleStatus(id);
        return ResponseEntity.ok(article);
    }

    @GetMapping("/{id}/analysis")
    public ResponseEntity<ApprovalResult> getAnalysis(@PathVariable UUID id) {
        ApprovalResult result = approvalService.getApprovalResult(id);
        if (result == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(result);
    }

    @PostMapping("/{id}/approve")
    public ResponseEntity<Article> manualApprove(
            @PathVariable UUID id,
            @RequestParam String approvedBy,
            @RequestParam(required = false) String feedback) {
        Article article = approvalService.manualApprove(id, approvedBy, feedback);
        return ResponseEntity.ok(article);
    }

    @PostMapping("/{id}/reject")
    public ResponseEntity<Article> manualReject(
            @PathVariable UUID id,
            @RequestParam String rejectedBy,
            @RequestParam String feedback) {
        Article article = approvalService.manualReject(id, rejectedBy, feedback);
        return ResponseEntity.ok(article);
    }
}
