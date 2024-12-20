package com.travelbuddy.persistence.repository;

import com.travelbuddy.persistence.domain.entity.BehaviorLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BehaviorLogRepository extends JpaRepository<BehaviorLogEntity, Integer> {
    List<BehaviorLogEntity> findByUserId(Integer userId);
}
