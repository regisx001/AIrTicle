package com.regisx001.validationsystem.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AiConfig {

    @Bean
    public AIAnalysisConfig aiAnalysisConfig() {
        return AIAnalysisConfig.builder()
                .minWordCount(100)
                .maxWordCount(10000)
                .confidenceThreshold(0.75)
                .autoApprovalThreshold(0.90)
                .autoRejectionThreshold(0.30)
                .plagiarismThreshold(0.85)
                .grammarScoreThreshold(0.70)
                .readabilityScoreThreshold(0.60)
                .build();
    }

    @Bean
    ChatClient ChatClient(ChatModel chatModel) {
        return ChatClient.builder(chatModel).defaultSystem("""
                You are an expert content reviewer for a blog platform.
                Your task is to analyze articles for quality, appropriateness, and adherence to editorial standards.
                Always provide constructive feedback and specific recommendations for improvement.
                Respond in JSON format with structured analysis results.
                """).build();
    }
}
