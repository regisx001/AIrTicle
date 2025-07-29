package com.regisx001.validationsystem.repositories;

import com.regisx001.validationsystem.domain.entities.AnalyseResult;
import com.regisx001.validationsystem.domain.enums.ApprovalDecision;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AnalyseResultRepository extends JpaRepository<AnalyseResult, UUID> {

    Optional<AnalyseResult> findByArticleId(UUID articleId);

    List<AnalyseResult> findByDecision(ApprovalDecision decision);

    @Query("SELECT AVG(ar.confidenceScore) FROM AnalyseResult ar WHERE ar.analyzedAt >= ?1")
    Double getAverageConfidenceScore(LocalDateTime since);

    @Query("SELECT ar FROM AnalyseResult ar WHERE ar.confidenceScore < ?1 AND ar.decision = 'APPROVED'")
    List<AnalyseResult> findLowConfidenceApprovals(Double threshold);
}
