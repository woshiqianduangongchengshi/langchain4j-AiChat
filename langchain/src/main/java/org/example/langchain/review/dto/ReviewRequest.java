package org.example.langchain.review.dto;

import lombok.Data;
import jakarta.validation.constraints.*;

@Data
public class ReviewRequest {
    @NotNull(message = "整体满意度不能为空")
    @Min(value = 1, message = "评分必须在1-5之间")
    @Max(value = 5, message = "评分必须在1-5之间")
    private Integer overallRating;
    
    @NotNull(message = "质量评分不能为空")
    @Min(value = 1, message = "评分必须在1-5之间")
    @Max(value = 5, message = "评分必须在1-5之间")
    private Integer qualityRating;
    
    @NotNull(message = "速度评分不能为空")
    @Min(value = 1, message = "评分必须在1-5之间")
    @Max(value = 5, message = "评分必须在1-5之间")
    private Integer speedRating;
    
    private String comment;
}