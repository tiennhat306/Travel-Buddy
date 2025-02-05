package com.travelbuddy.persistence.repository;

import com.travelbuddy.persistence.domain.entity.SiteReviewEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

public interface SiteReviewRepository extends JpaRepository<SiteReviewEntity, Integer>, JpaSpecificationExecutor<SiteReviewEntity> {
    @Query("SELECT COALESCE(AVG(sre.generalRating), 0) FROM SiteReviewEntity sre WHERE sre.siteId = ?1")
    Double getAverageGeneralRatingBySiteId(int siteId);

    @Query("SELECT COUNT(sre) FROM SiteReviewEntity sre WHERE sre.siteId = ?1")
    int countBySiteId(int siteId);

    @Query("SELECT COUNT(sre) FROM SiteReviewEntity sre WHERE sre.siteId = ?1 AND sre.generalRating = ?2")
    int countBySiteIdAndGeneralRating(int siteId, int generalRating);

    boolean existsBySiteIdAndUserId(int siteId, int userId);

    Page<SiteReviewEntity> findAllByUserIdAndCommentContainingIgnoreCase(int userId, String reviewSearch, Pageable pageable);

    @Query("SELECT sre.siteId FROM SiteReviewEntity sre WHERE sre.id = ?1")
    int getSiteIdById(int id);

    @Query("SELECT sre FROM SiteReviewEntity sre WHERE sre.siteId = ?1 AND sre.userId = ?2")
    SiteReviewEntity findBySiteIdAndUserId(int entityId, int userId);

    @Query("SELECT sre FROM SiteReviewEntity sre WHERE sre.siteId = ?1 ORDER BY sre.createdAt DESC LIMIT 1")
    SiteReviewEntity findFirstBySiteIdOrderByCreatedAtDesc(Integer entityId);
}
