package com.travelbuddy.persistence.repository;

import com.travelbuddy.persistence.domain.entity.GroupEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GroupRepository extends JpaRepository<GroupEntity, Long> {
    Optional<GroupEntity> findByName(String name);
    Page<GroupEntity> findAllByNameContainingIgnoreCase(String search, Pageable pageable);
    boolean existsByNameIgnoreCase(String name);
}
