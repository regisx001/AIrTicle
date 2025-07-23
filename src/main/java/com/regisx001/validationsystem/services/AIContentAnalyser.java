package com.regisx001.validationsystem.services;

import com.regisx001.validationsystem.domain.entities.ApprovalResult;
import com.regisx001.validationsystem.domain.entities.Article;

public interface AIContentAnalyser {
    public ApprovalResult analyzeContent(Article article);
}
