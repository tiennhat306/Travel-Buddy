package com.travelbuddy.persistence.repository;

import com.travelbuddy.persistence.domain.entity.TravelPlanEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TravelPlanRepository extends JpaRepository<TravelPlanEntity, Integer> {
    @Query("SELECT tp FROM TravelPlanEntity tp JOIN tp.userEntities tu WHERE tu.id = :userId ORDER BY tp.startTime")
    List<TravelPlanEntity> findAllByUserId(int userId);

    @Query("SELECT tu.id FROM TravelPlanEntity tp JOIN tp.userEntities tu WHERE tp.id = :entityId")
    List<Integer> findUserIdsById(int entityId);
}
