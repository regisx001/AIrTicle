package com.regisx001.validationsystem.services.impl;

import java.util.UUID;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.regisx001.validationsystem.domain.entities.ApprovalResult;
import com.regisx001.validationsystem.domain.entities.Article;
import com.regisx001.validationsystem.repositories.ApprovalResultRepository;
import com.regisx001.validationsystem.repositories.ArticleRepository;
import com.regisx001.validationsystem.services.AIApproveService;
import com.regisx001.validationsystem.utils.ArticleUtils;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AIApproveServiceImpl implements AIApproveService {

    private final ArticleUtils utils;
    private final ApprovalResultRepository approvalResultRepository;
    private final ArticleRepository articleRepository;
    private final ChatClient chatClient;

    @Async
    @Override
    public void analyseArticle(Article article) {
        long startTime = System.currentTimeMillis();
        if (!utils.isValidForAnalysis(article)) {
            throw new RuntimeException("Article doesn't meet basic requirements by system-analysis");
        }

        String prompt = utils.buildAnalysisPrompt(article);
        String aiResponse = chatClient.prompt(prompt).call().content();
        long endTime = System.currentTimeMillis();
        Integer analyzeTimeMs = (int) (endTime - startTime);

        ApprovalResult result = utils.buildApprovalResult(article, utils.parseAIResponse(aiResponse), analyzeTimeMs);
        approvalResultRepository.save(result);
        // return result;
    }

    @Override
    public ApprovalResult getLatestApprovalResult(UUID id) {
        ApprovalResult result = approvalResultRepository.findByArticleId(id)
                .orElseThrow(() -> new RuntimeException("Not Result for this article"));
        return result;
    }

    @Override
    public ApprovalResult analyseArticleManual(UUID id) {
        Article article = articleRepository.findById(id).orElseThrow(() -> new RuntimeException("Article not found"));
        long startTime = System.currentTimeMillis();
        if (!utils.isValidForAnalysis(article)) {
            throw new RuntimeException("Article doesn't meet basic requirements by system-analysis");
        }

        String prompt = utils.buildAnalysisPrompt(article);
        String aiResponse = chatClient.prompt(prompt).call().content();
        long endTime = System.currentTimeMillis();
        Integer analyzeTimeMs = (int) (endTime - startTime);

        ApprovalResult result = utils.buildApprovalResult(article, utils.parseAIResponse(aiResponse), analyzeTimeMs);
        return result;
    }

}
