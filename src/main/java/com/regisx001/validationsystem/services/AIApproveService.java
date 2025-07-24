package com.regisx001.validationsystem.services;

import java.util.UUID;

import com.regisx001.validationsystem.domain.entities.ApprovalResult;
import com.regisx001.validationsystem.domain.entities.Article;

public interface AIApproveService {
    void analyseArticle(Article article);

    ApprovalResult getLatestApprovalResult(UUID id);
}
