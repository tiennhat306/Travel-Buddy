package com.travelbuddy.persistence.repository;

import com.travelbuddy.persistence.domain.entity.PermissionEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PermissionRepository extends JpaRepository<PermissionEntity, Long> {
    Optional<PermissionEntity> findByName(String name);
    Page<PermissionEntity> findAllByNameContainingIgnoreCase(String search, Pageable pageable);
    List<PermissionEntity> findAll();
}
