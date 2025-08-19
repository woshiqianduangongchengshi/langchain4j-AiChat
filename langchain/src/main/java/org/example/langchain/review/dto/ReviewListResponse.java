package org.example.langchain.review.dto;

import lombok.Data;
import java.util.List;

@Data
public class ReviewListResponse {
    private List<ReviewResponse> reviews;
    private Integer page;
    private Integer totalPages;
}