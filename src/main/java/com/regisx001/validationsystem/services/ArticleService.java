package com.regisx001.validationsystem.services;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.regisx001.validationsystem.domain.entities.Article;

public interface ArticleService {
    Article createArticle(Article article);

    Article getArticleById(UUID id);

    Article updateArticle(UUID id, Article article);

    void deleteArticle(UUID id);

    Page<Article> getAllArticles(Pageable pageable);
}
