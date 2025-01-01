package com.travelbuddy.persistence.repository;

import com.travelbuddy.persistence.domain.entity.SiteEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import java.util.Optional;

public interface SiteRepository extends JpaRepository<SiteEntity, Long>, JpaSpecificationExecutor<SiteEntity> {
    Optional<SiteEntity> findById(Integer siteId);
}