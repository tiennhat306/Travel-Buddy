package com.travelbuddy.persistence.repository;

import com.travelbuddy.persistence.domain.dto.report.admin.SiteReviewReportRspnDto;
import com.travelbuddy.persistence.domain.entity.SiteReviewReportEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SiteReviewReportRepository extends JpaRepository<SiteReviewReportEntity, Integer> {
    @Query("SELECT s FROM SiteReviewReportEntity s WHERE s.siteReviewId = ?1 AND s.reportEntity.reportedBy = ?2")
    SiteReviewReportEntity findBySiteReviewIdAndUserId(Integer siteReviewId, int userIdCurrentRequest);

    @Query("""
        SELECT new com.travelbuddy.persistence.domain.dto.report.admin.SiteReviewReportRspnDto(
            srr.siteReviewId,
            srr.siteReviewEntity.comment,
            (
                SELECT CASE
                    WHEN rm IS NULL THEN NULL
                    WHEN rm.media IS NULL THEN NULL
                    ELSE rm.media.url
                END
                FROM ReviewMediaEntity rm
                WHERE rm.review.id = srr.siteReviewId
                AND rm.mediaType = 'IMAGE'
                AND rm.id = (
                    SELECT MIN(rm2.id)
                    FROM ReviewMediaEntity rm2
                    WHERE rm2.review.id = srr.siteReviewId
                    AND rm2.mediaType = 'IMAGE'
                )
            ),
            COUNT(srr.reportId)
        )
        FROM SiteReviewReportEntity srr
        WHERE LOWER(srr.siteReviewEntity.comment) LIKE LOWER(CONCAT('%', :search, '%'))
        AND srr.siteReviewEntity.enabled = true
        GROUP BY srr.siteReviewId, srr.siteReviewEntity.comment
        ORDER BY COUNT(srr.reportId) DESC
    """)
    Page<SiteReviewReportRspnDto> findAllBySiteNameAndPageable(@Param("search") String search, Pageable pageable);

    @Query("""
        SELECT new com.travelbuddy.persistence.domain.dto.report.admin.SiteReviewReportRspnDto(
            srr.siteReviewId,
            srr.siteReviewEntity.comment,
            (
                SELECT CASE
                    WHEN rm IS NULL THEN NULL
                    WHEN rm.media IS NULL THEN NULL
                    ELSE rm.media.url
                END
                FROM ReviewMediaEntity rm
                WHERE rm.review.id = srr.siteReviewId
                AND rm.mediaType = 'IMAGE'
                AND rm.id = (
                    SELECT MIN(rm2.id)
                    FROM ReviewMediaEntity rm2
                    WHERE rm2.review.id = srr.siteReviewId
                    AND rm2.mediaType = 'IMAGE'
                )
            ),
            COUNT(srr.reportId)
        )
        FROM SiteReviewReportEntity srr
        WHERE LOWER(srr.siteReviewEntity.comment) LIKE LOWER(CONCAT('%', :search, '%'))
        AND srr.siteReviewEntity.enabled = false
        GROUP BY srr.siteReviewId, srr.siteReviewEntity.comment
        ORDER BY COUNT(srr.reportId) DESC
    """)
    Page<SiteReviewReportRspnDto> findAllBannedBySiteNameAndPageable(@Param("search") String search, Pageable pageable);

    @Query("SELECT s FROM SiteReviewReportEntity s WHERE s.siteReviewId = ?1 ORDER BY s.reportEntity.createdAt DESC")
    Page<SiteReviewReportEntity> findAllBySiteReviewId(Integer siteReviewId, Pageable pageable);
}
