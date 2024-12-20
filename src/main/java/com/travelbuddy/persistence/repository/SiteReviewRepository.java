package com.travelbuddy.persistence.repository;

import com.travelbuddy.persistence.domain.dto.sitereview.MySiteReviewRspnDto;
import com.travelbuddy.persistence.domain.entity.SiteReviewEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

public interface SiteReviewRepository extends JpaRepository<SiteReviewEntity, Long>, JpaSpecificationExecutor<SiteReviewEntity> {
    @Query("SELECT COALESCE(AVG(sre.generalRating), 0) FROM SiteReviewEntity sre WHERE sre.siteId = ?1")
    Double getAverageGeneralRatingBySiteId(int siteId);

    @Query("SELECT COUNT(sre) FROM SiteReviewEntity sre WHERE sre.siteId = ?1")
    int countBySiteId(int siteId);

    @Query("SELECT COUNT(sre) FROM SiteReviewEntity sre WHERE sre.siteId = ?1 AND sre.generalRating = ?2")
    int countBySiteIdAndGeneralRating(int siteId, int i);

    boolean existsBySiteIdAndUserId(int siteId, int userId);

    Page<SiteReviewEntity> findAllByUserIdAndCommentContainingIgnoreCase(int userId, String reviewSearch, Pageable pageable);
    int getSiteIdById(int reviewId);
}
