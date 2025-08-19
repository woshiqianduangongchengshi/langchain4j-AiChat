package org.example.langchain.review.dto;

import lombok.Data;

@Data
public class ReviewStatsResponse {
    private Double averageOverallRating;
    private Double averageQualityRating;
    private Double averageSpeedRating;
    private Long totalReviews;
}