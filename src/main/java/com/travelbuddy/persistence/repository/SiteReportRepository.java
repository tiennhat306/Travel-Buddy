package com.travelbuddy.persistence.repository;

import com.travelbuddy.persistence.domain.dto.report.admin.SiteReportRspnDto;
import com.travelbuddy.persistence.domain.entity.SiteReportEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SiteReportRepository extends JpaRepository<SiteReportEntity, Integer> {
    @Query("SELECT s FROM SiteReportEntity s WHERE s.siteId = ?1 AND s.reportEntity.reportedBy = ?2")
    SiteReportEntity findBySiteIdAndUserId(Integer siteId, int userIdCurrentRequest);


//    @Query("SELECT sa.siteVersionId FROM SiteApprovalEntity sa " +
//            "JOIN SiteVersionEntity sv ON sa.siteVersionId = sv.id " +
//            "WHERE sa.status = 'APPROVED' " +
//            "AND sv.siteId = :siteId " +
//            "ORDER BY sv.createdAt DESC " +
//            "LIMIT 1")

    // query to get siteReportRspnDto by siteName, but need to get from site version with lastest approved: site join siteVersion and get the lastest approved by check where with above sql
//    @Query("SELECT new com.travelbuddy.persistence.domain.dto.report.admin.SiteReportRspnDto(s.id, s.siteId, s.siteName, s.reportEntity.reportedBy, s.reportEntity.reportedAt, s.reportEntity.reportReason, s.reportEntity.reportStatus) FROM SiteReportEntity s WHERE lower(s.siteName) like lower(concat('%', :reviewSearch, '%'))")

    @Query("""
        SELECT new com.travelbuddy.persistence.domain.dto.report.admin.SiteReportRspnDto(
            sr.siteId,
            sv.siteName,
            (SELECT CASE
                               WHEN sm IS NULL THEN NULL
                               WHEN sm.media IS NULL THEN NULL
                               ELSE sm.media.url
                           END
             FROM SiteMediaEntity sm
             WHERE sm.site.id = sr.siteId
               AND sm.mediaType = 'IMAGE'
               AND sm.id = (SELECT MIN(sm2.id)
                            FROM SiteMediaEntity sm2
                            WHERE sm2.site.id = sr.siteId
                              AND sm2.mediaType = 'IMAGE')),
            COUNT(sr.reportId)
        )
        FROM SiteReportEntity sr
        JOIN sr.siteEntity s
        JOIN SiteVersionEntity sv ON sv.siteId = s.id
        WHERE sv.id = (SELECT MAX(sv2.id)
                       FROM SiteVersionEntity sv2
                       WHERE sv2.siteId = s.id
                         AND sv2.siteApprovalEntity.status = 'APPROVED')
          AND LOWER(sv.siteName) LIKE LOWER(CONCAT('%', :siteNameSearch, '%'))
          AND s.enabled = true
        GROUP BY sr.siteId, sv.siteName
        ORDER BY COUNT(sr.reportId) DESC
    """)
    Page<SiteReportRspnDto> findAllBySiteNameAndPageable(@Param("siteNameSearch") String reviewSearch, Pageable pageable);

    @Query("""
        SELECT new com.travelbuddy.persistence.domain.dto.report.admin.SiteReportRspnDto(
            sr.siteId,
            sv.siteName,
            (SELECT CASE
                               WHEN sm IS NULL THEN NULL
                               WHEN sm.media IS NULL THEN NULL
                               ELSE sm.media.url
                           END
             FROM SiteMediaEntity sm
             WHERE sm.site.id = sr.siteId
               AND sm.mediaType = 'IMAGE'
               AND sm.id = (SELECT MIN(sm2.id)
                            FROM SiteMediaEntity sm2
                            WHERE sm2.site.id = sr.siteId
                              AND sm2.mediaType = 'IMAGE')),
            COUNT(sr.reportId)
        )
        FROM SiteReportEntity sr
        JOIN sr.siteEntity s
        JOIN SiteVersionEntity sv ON sv.siteId = s.id
        WHERE sv.id = (SELECT MAX(sv2.id)
                       FROM SiteVersionEntity sv2
                       WHERE sv2.siteId = s.id
                         AND sv2.siteApprovalEntity.status = 'APPROVED')
          AND LOWER(sv.siteName) LIKE LOWER(CONCAT('%', :siteNameSearch, '%'))
          AND s.enabled = false
        GROUP BY sr.siteId, sv.siteName
        ORDER BY COUNT(sr.reportId) DESC
    """)
    Page<SiteReportRspnDto> findAllBannedBySiteNameAndPageable(@Param("siteNameSearch") String search, Pageable pageable);

    @Query("SELECT s FROM SiteReportEntity s WHERE s.siteId = ?1 ORDER BY s.reportEntity.createdAt DESC")
    Page<SiteReportEntity> findAllBySiteId(Integer siteId, Pageable pageable);

}
