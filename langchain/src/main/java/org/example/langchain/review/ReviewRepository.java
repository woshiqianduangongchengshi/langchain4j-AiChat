package org.example.langchain.review;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    
    @Query("SELECT AVG(r.overallRating) FROM Review r WHERE r.featureType = :featureType")
    Double findAverageOverallRatingByFeatureType(@Param("featureType") String featureType);
    
    @Query("SELECT AVG(r.qualityRating) FROM Review r WHERE r.featureType = :featureType")
    Double findAverageQualityRatingByFeatureType(@Param("featureType") String featureType);
    
    @Query("SELECT AVG(r.speedRating) FROM Review r WHERE r.featureType = :featureType")
    Double findAverageSpeedRatingByFeatureType(@Param("featureType") String featureType);
    
    @Query("SELECT COUNT(r) FROM Review r WHERE r.featureType = :featureType")
    Long countByFeatureType(@Param("featureType") String featureType);
    
    Page<Review> findByFeatureTypeOrderByCreatedAtDesc(String featureType, Pageable pageable);
}