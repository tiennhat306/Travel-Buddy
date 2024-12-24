package com.travelbuddy.persistence.repository;

import com.travelbuddy.persistence.domain.entity.SiteMediaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SiteMediaRepository extends JpaRepository<SiteMediaEntity, Long> {
    void deleteByMediaIdIn(List<String> mediaIdsToDelete);

    void deleteBySiteId(Integer siteId);

    List<SiteMediaEntity> findAllBySiteId(Integer siteId);
    // Get the first SiteMediaEntity by siteId and mediaType
    SiteMediaEntity findFirstBySiteIdAndMediaType(Integer siteId, String mediaType);
}
