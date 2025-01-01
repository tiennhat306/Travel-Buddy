package com.travelbuddy.persistence.repository;

import com.travelbuddy.persistence.domain.entity.LogEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LogRepository extends JpaRepository<LogEntity, Integer> {
    Page<LogEntity> findAllByOrderByTimestampDesc(Pageable pageable);
    // find all by order by timestamp desc, or content like %searchText% or level like %searchText%
    Page<LogEntity> findAllByContentContainingOrLevelContainingOrderByTimestampDesc(String content, String level, Pageable pageable);
}
