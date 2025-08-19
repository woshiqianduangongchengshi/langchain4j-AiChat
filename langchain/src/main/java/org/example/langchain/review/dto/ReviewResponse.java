package org.example.langchain.review.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ReviewResponse {
    private Long id;
    private String username;
    private String featureType;
    private Integer overallRating;
    private Integer qualityRating;
    private Integer speedRating;
    private String comment;
    private LocalDateTime createdAt;
}