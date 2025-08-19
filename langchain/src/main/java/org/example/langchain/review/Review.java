package org.example.langchain.review;

import jakarta.persistence.*;
import lombok.Data;
import org.example.langchain.customer.User;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "reviews")
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
    
    @Column(name = "feature_type", length = 20)
    private String featureType; // "chat" 或 "image"
    
    @Column(name = "overall_rating")
    private Integer overallRating;
    
    @Column(name = "quality_rating")
    private Integer qualityRating;
    
    @Column(name = "speed_rating")
    private Integer speedRating;
    
    @Column(name = "comment", columnDefinition = "TEXT")
    private String comment;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
}