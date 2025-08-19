package org.example.langchain.review;

import org.example.langchain.customer.User;
import org.example.langchain.review.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ReviewService {
    
    @Autowired
    private ReviewRepository reviewRepository;
    
    public ReviewResponse submitReview(User user, String featureType, ReviewRequest request) {
        Review review = new Review();
        review.setUser(user);
        review.setFeatureType(featureType);
        review.setOverallRating(request.getOverallRating());
        review.setQualityRating(request.getQualityRating());
        review.setSpeedRating(request.getSpeedRating());
        review.setComment(request.getComment());
        
        Review savedReview = reviewRepository.save(review);
        return mapToReviewResponse(savedReview);
    }
    
    public ReviewStatsResponse getReviewStats(String featureType) {
        ReviewStatsResponse stats = new ReviewStatsResponse();
        stats.setAverageOverallRating(reviewRepository.findAverageOverallRatingByFeatureType(featureType));
        stats.setAverageQualityRating(reviewRepository.findAverageQualityRatingByFeatureType(featureType));
        stats.setAverageSpeedRating(reviewRepository.findAverageSpeedRatingByFeatureType(featureType));
        stats.setTotalReviews(reviewRepository.countByFeatureType(featureType));
        return stats;
    }
    
    public ReviewListResponse getReviewList(String featureType, int page, int pageSize) {
        Pageable pageable = PageRequest.of(page - 1, pageSize);
        Page<Review> reviewPage = reviewRepository.findByFeatureTypeOrderByCreatedAtDesc(featureType, pageable);
        
        List<ReviewResponse> reviews = reviewPage.getContent().stream()
            .map(this::mapToReviewResponse)
            .collect(Collectors.toList());
        
        ReviewListResponse response = new ReviewListResponse();
        response.setReviews(reviews);
        response.setPage(page);
        response.setTotalPages(reviewPage.getTotalPages());
        
        return response;
    }
    
    private ReviewResponse mapToReviewResponse(Review review) {
        ReviewResponse response = new ReviewResponse();
        response.setId(review.getId());
        response.setUsername(review.getUser().getUsername());
        response.setFeatureType(review.getFeatureType());
        response.setOverallRating(review.getOverallRating());
        response.setQualityRating(review.getQualityRating());
        response.setSpeedRating(review.getSpeedRating());
        response.setComment(review.getComment());
        response.setCreatedAt(review.getCreatedAt());
        return response;
    }
}