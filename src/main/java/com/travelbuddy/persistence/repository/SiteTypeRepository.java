package com.travelbuddy.persistence.repository;

import com.travelbuddy.persistence.domain.entity.SiteTypeEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface SiteTypeRepository extends JpaRepository<SiteTypeEntity, Integer> {
    boolean existsByTypeNameIgnoreCase(String typeName);

    Page<SiteTypeEntity> searchSiteTypeEntitiesByTypeNameContainingIgnoreCase(String typeName, Pageable pageable);

    Optional<Integer> findIdByTypeNameIgnoreCase(String typeName);

    Optional<List<SiteTypeEntity>> findAllByTypeNameIgnoreCase(String typeName);
}
