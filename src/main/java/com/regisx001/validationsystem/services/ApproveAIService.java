package com.regisx001.validationsystem.services;

import java.util.UUID;

import com.regisx001.validationsystem.domain.entities.ApprovalResult;

public interface ApproveAIService {
    public ApprovalResult analyseArticle(UUID id);

    public String getUsedLLM();
}
