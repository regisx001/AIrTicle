package com.regisx001.validationsystem.repositories;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.regisx001.validationsystem.domain.entities.AnalyseHistory;

@Repository
public interface AnalyseHistoryRepository extends JpaRepository<AnalyseHistory, UUID> {

    // Use article.id to reference the ID of the related article
    List<AnalyseHistory> findByArticle_IdOrderByCreatedAtDesc(UUID articleId);

}
