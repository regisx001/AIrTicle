package com.regisx001.validationsystem.domain.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.regisx001.validationsystem.domain.enums.ApprovalDecision;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Entity
@Table(name = "approval_results")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApprovalResult {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private UUID articleId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ApprovalDecision decision;

    @Column(nullable = false)
    private Double confidenceScore;

    @Column(columnDefinition = "TEXT")
    private String aiAnalysis;

    @ElementCollection
    @CollectionTable(name = "flagged_issues", joinColumns = @JoinColumn(name = "approval_result_id"))
    @Column(name = "issue")
    private List<String> flaggedIssues;

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
