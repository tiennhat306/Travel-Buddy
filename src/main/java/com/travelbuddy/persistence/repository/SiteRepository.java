package com.travelbuddy.persistence.repository;

import com.travelbuddy.persistence.domain.entity.SiteEntity;
import com.travelbuddy.persistence.domain.entity.SiteReactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface SiteRepository extends JpaRepository<SiteEntity, Long>, JpaSpecificationExecutor<SiteEntity> {
    Optional<SiteEntity> findById(Integer siteId);
}