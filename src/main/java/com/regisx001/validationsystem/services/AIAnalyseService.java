package com.regisx001.validationsystem.services;

import java.util.UUID;

import com.regisx001.validationsystem.domain.entities.AnalyseResult;

public interface AIAnalyseService {
    void analyseArticle(UUID id);

    AnalyseResult analyseArticleManual(UUID id);

    AnalyseResult getLatestApprovalResult(UUID id);
}
