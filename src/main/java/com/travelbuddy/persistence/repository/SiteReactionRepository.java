package com.travelbuddy.persistence.repository;

import com.travelbuddy.persistence.domain.entity.SiteReactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface SiteReactionRepository extends JpaRepository<SiteReactionEntity, Long> {
    Optional<SiteReactionEntity> findByUserIdAndSiteId(int userId, int siteId);

    int countBySiteIdAndReactionType(int siteId, String like);

    @Query("SELECT sre FROM SiteReactionEntity sre WHERE sre.siteId = ?1 AND sre.reactionType = ?2 ORDER BY sre.createdAt DESC LIMIT 1")
    SiteReactionEntity findFirstBySiteIdOrderByCreatedAtDesc(Integer entityId, String reactionType);
}
