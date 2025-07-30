package com.regisx001.validationsystem.domain.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.regisx001.validationsystem.domain.enums.AnalyseAction;
import com.regisx001.validationsystem.domain.enums.ArticleStatus;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@Table(name = "analyse_histories")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnalyseHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "article_id", nullable = false)
    @JsonIgnore
    private Article article;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AnalyseAction action;

    @Enumerated(EnumType.STRING)
    private ArticleStatus fromStatus;

    @Enumerated(EnumType.STRING)
    private ArticleStatus toStatus;

    @Column(nullable = false)
    private String performedBy;

    @Column(columnDefinition = "TEXT")
    private String reason;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(columnDefinition = "TEXT")
    private String metadata;

    private Double confidenceScore;
    private String aiModel;
    private Integer processingTimeMs;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @JsonProperty("articleId")
    public UUID getArticleId() {
        return article != null ? article.getId() : null;
    }

    @PrePersist
    public void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
