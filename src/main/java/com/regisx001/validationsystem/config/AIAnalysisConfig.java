package com.regisx001.validationsystem.config;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AIAnalysisConfig {
    private Integer minWordCount;
    private Integer maxWordCount;
    private Double confidenceThreshold;
    private Double autoApprovalThreshold;
    private Double autoRejectionThreshold;
    private Double plagiarismThreshold;
    private Double grammarScoreThreshold;
    private Double readabilityScoreThreshold;

    // Analysis weights
    @Builder.Default
    private Double contentQualityWeight = 0.30;

    @Builder.Default
    private Double grammarWeight = 0.25;

    @Builder.Default
    private Double originalityWeight = 0.25;

    @Builder.Default
    private Double seoWeight = 0.20;
}
