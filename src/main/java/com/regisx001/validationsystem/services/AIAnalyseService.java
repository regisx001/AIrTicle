package com.regisx001.validationsystem.services;

import java.util.UUID;

import com.regisx001.validationsystem.domain.entities.AnalyseResult;
import com.regisx001.validationsystem.domain.entities.Article;

public interface AIAnalyseService {
    void analyseArticle(Article article);

    AnalyseResult analyseArticleManual(UUID id);

    AnalyseResult getLatestApprovalResult(UUID id);
}
