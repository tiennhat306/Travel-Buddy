package com.travelbuddy.persistence.repository;

import com.travelbuddy.common.constants.ApprovalStatusEnum;
import com.travelbuddy.common.paging.PageDto;
import com.travelbuddy.persistence.domain.entity.SiteVersionEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SiteVersionRepository extends JpaRepository<SiteVersionEntity, Integer> {
    // get site type from site version id
    Integer findTypeIdById(Integer id);

    Optional<SiteVersionEntity> findById(Integer id);

    @Query("SELECT sv " +
            "FROM SiteVersionEntity sv " +
            "JOIN SiteApprovalEntity sa ON sv.id = sa.siteVersionId " +
            "WHERE sa.status = 'APPROVED' " +
            "AND sv.lat BETWEEN :lat - :degRadius AND :lat + :degRadius " +
            "AND ((:lng - :degRadius < -180 AND sv.lng BETWEEN :lng - :degRadius + 360 AND 180) " +
            "OR (:lng + :degRadius > 180 AND sv.lng BETWEEN -180 AND :lng + :degRadius - 360) " +
            "OR (sv.lng BETWEEN :lng - :degRadius AND :lng + :degRadius)) " +
            "AND sv.createdAt = (SELECT MAX(sv2.createdAt) FROM SiteVersionEntity sv2 WHERE sv2.siteId = sv.siteId) " +
            "GROUP BY sv.id, sv.siteId, sv.lat, sv.lng, sv.createdAt")
    List<SiteVersionEntity> findApprovedSiteVersionsInRange(@Param("lat") double lat,
                                                            @Param("lng") double lng,
                                                            @Param("degRadius") double degRadius);

    Page<SiteVersionEntity> findAll(Specification<SiteVersionEntity> spec, Pageable pageable);

    // Get all site versions by owner id
    @Query("SELECT sv FROM SiteVersionEntity sv JOIN SiteEntity s ON sv.siteId = s.id WHERE s.ownerId = :ownerId")
    Page<SiteVersionEntity> findAllByOwnerId(Integer ownerId, Pageable pageable);

}
