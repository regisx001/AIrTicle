package com.regisx001.validationsystem.repositories;

import com.regisx001.validationsystem.domain.entities.Article;
import com.regisx001.validationsystem.domain.enums.ArticleStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface ArticleRepository extends JpaRepository<Article, UUID> {

    List<Article> findByStatus(ArticleStatus status);

    Page<Article> findByStatusIn(List<ArticleStatus> statuses, Pageable pageable);

    List<Article> findByStatusAndCreatedAtBefore(ArticleStatus status, LocalDateTime timestamp);

    @Query("SELECT a FROM Article a WHERE a.status = 'SUBMITTED_FOR_APPROVAL' ORDER BY a.createdAt ASC")
    List<Article> findPendingApprovalQueue();

    @Query("SELECT COUNT(a) FROM Article a WHERE a.status = ?1")
    Long countByStatus(ArticleStatus status);

    @Query("SELECT a FROM Article a WHERE a.isPublished = true ORDER BY a.publishedAt DESC")
    Page<Article> findPublishedArticles(Pageable pageable);
}
