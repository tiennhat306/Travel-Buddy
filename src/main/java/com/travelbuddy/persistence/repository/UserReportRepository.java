package com.travelbuddy.persistence.repository;

import com.travelbuddy.persistence.domain.dto.report.admin.UserReportRspnDto;
import com.travelbuddy.persistence.domain.entity.UserReportEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserReportRepository extends JpaRepository<UserReportEntity, Integer> {
    @Query("SELECT u FROM UserReportEntity u WHERE u.userId = ?1 AND u.reportEntity.reportedBy = ?2")
    UserReportEntity findByUserIdAndUserId(Integer userId, int userIdCurrentRequest);

    @Query("""
        SELECT new com.travelbuddy.persistence.domain.dto.report.admin.UserReportRspnDto(
            u.userId,
            u.userEntity.nickname,
            (
                SELECT CASE
                    WHEN f IS NULL THEN NULL
                    ELSE f.url
                END
                FROM FileEntity f
                WHERE f.user.id = u.userId
            ),
            COUNT(u.reportId)
        )
        FROM UserReportEntity u
        WHERE LOWER(u.userEntity.nickname) LIKE LOWER(CONCAT('%', :search, '%'))
            AND u.userEntity.enabled = true
        GROUP BY u.userId, u.userEntity.nickname
        ORDER BY COUNT(u.reportId) DESC
    """)
    Page<UserReportRspnDto> findAllByUserNicknameAndPageable(@Param("search") String search, Pageable pageable);

    @Query("""
        SELECT new com.travelbuddy.persistence.domain.dto.report.admin.UserReportRspnDto(
            u.userId,
            u.userEntity.nickname,
            (
                SELECT CASE
                    WHEN f IS NULL THEN NULL
                    ELSE f.url
                END
                FROM FileEntity f
                WHERE f.user.id = u.userId
            ),
            COUNT(u.reportId)
        )
        FROM UserReportEntity u
        WHERE LOWER(u.userEntity.nickname) LIKE LOWER(CONCAT('%', :search, '%'))
            AND u.userEntity.enabled = false
        GROUP BY u.userId, u.userEntity.nickname
        ORDER BY COUNT(u.reportId) DESC
    """)
    Page<UserReportRspnDto> findAllBannedByUserNicknameAndPageable(@Param("search") String search, Pageable pageable);

    @Query("SELECT u FROM UserReportEntity u WHERE u.userId = ?1 ORDER BY u.reportEntity.createdAt DESC")
    Page<UserReportEntity> findAllByUserId(Integer userId, Pageable pageable);
}
