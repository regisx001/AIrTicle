package com.regisx001.validationsystem.domain.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.regisx001.validationsystem.domain.enums.AnalyseDecision;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@Table(name = "analyse_results")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnalyseResult {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "article_id", nullable = false)
    @JsonIgnore
    private Article article;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AnalyseDecision decision;

    @Column(nullable = false)
    private Double confidenceScore;

    @Column(columnDefinition = "TEXT")
    private String aiAnalysis;

    @Column(columnDefinition = "TEXT")
    private String recommendations;

    // Quality Metrics
    private Double readabilityScore;
    private Double grammarScore;
    private Double seoScore;
    private Double originalityScore;

    @Column(nullable = false)
    private LocalDateTime analyzedAt;

    @Column(nullable = false)
    private String aiModel; // e.g., "gpt-4", "claude-3"

    private Integer processingTimeMs;

    @PrePersist
    public void onCreate() {
        this.analyzedAt = LocalDateTime.now();
    }
}
