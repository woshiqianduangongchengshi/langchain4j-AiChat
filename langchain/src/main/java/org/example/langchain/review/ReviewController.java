package org.example.langchain.review;

import org.example.langchain.customer.User;
import org.example.langchain.review.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/reviews")
public class ReviewController {
    
    @Autowired
    private ReviewService reviewService;
    
    @PostMapping("/chat")
    public ResponseEntity<ApiResponse<ReviewResponse>> submitChatReview(
            @AuthenticationPrincipal User user,
            @RequestBody ReviewRequest request) {
        ReviewResponse review = reviewService.submitReview(user, "chat", request);
        return ResponseEntity.ok(ApiResponse.success("评价提交成功", review));
    }
    
    @PostMapping("/image")
    public ResponseEntity<ApiResponse<ReviewResponse>> submitImageReview(
            @AuthenticationPrincipal User user,
            @RequestBody ReviewRequest request) {
        ReviewResponse review = reviewService.submitReview(user, "image", request);
        return ResponseEntity.ok(ApiResponse.success("评价提交成功", review));
    }
    
    @GetMapping("/chat/stats")
    public ResponseEntity<ApiResponse<ReviewStatsResponse>> getChatStats() {
        ReviewStatsResponse stats = reviewService.getReviewStats("chat");
        return ResponseEntity.ok(ApiResponse.success("获取成功", stats));
    }
    
    @GetMapping("/image/stats")
    public ResponseEntity<ApiResponse<ReviewStatsResponse>> getImageStats() {
        ReviewStatsResponse stats = reviewService.getReviewStats("image");
        return ResponseEntity.ok(ApiResponse.success("获取成功", stats));
    }
    
    @GetMapping("/chat/list")
    public ResponseEntity<ApiResponse<ReviewListResponse>> getChatReviews(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "3") int pageSize) {
        ReviewListResponse reviews = reviewService.getReviewList("chat", page, pageSize);
        return ResponseEntity.ok(ApiResponse.success("获取成功", reviews));
    }
    
    @GetMapping("/image/list")
    public ResponseEntity<ApiResponse<ReviewListResponse>> getImageReviews(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "3") int pageSize) {
        ReviewListResponse reviews = reviewService.getReviewList("image", page, pageSize);
        return ResponseEntity.ok(ApiResponse.success("获取成功", reviews));
    }
}