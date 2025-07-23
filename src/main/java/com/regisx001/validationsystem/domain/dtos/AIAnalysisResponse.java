package com.regisx001.validationsystem.domain.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class AIAnalysisResponse {
    private Double overallScore;
    private String recommendation;
    private String feedback;
    private List<String> recommendations; // Changed from String to List<String>

    private ContentQuality contentQuality;
    private Grammar grammar;
    private SEO seo;
    private Appropriateness appropriateness; // Added missing field

    private List<String> flaggedIssues;
    private Integer estimatedReadTime; // Added missing field

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ContentQuality {
        private Double score;
        private String feedback;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Grammar {
        private Double score;
        private String feedback;
        private List<String> issues;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class SEO {
        private Double score;
        private List<String> suggestions;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Appropriateness {
        private Double score;
        private String feedback;
    }
}
