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
    private String recommendations;

    private ContentQuality contentQuality;
    private Grammar grammar;
    private SEO seo;

    private List<String> flaggedIssues;

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
}
