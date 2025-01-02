package com.travelbuddy.persistence.repository;

import com.travelbuddy.persistence.domain.entity.TravelPlanUserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface TravelPlanUserRepository extends JpaRepository<TravelPlanUserEntity, Integer> {
    @Query("SELECT CASE WHEN COUNT(tpu) > 0 THEN TRUE ELSE FALSE END FROM TravelPlanUserEntity tpu WHERE tpu.id.travelPlanEntity.id = :travelPlanId AND tpu.id.userEntity.id = :userId AND tpu.role = 'OWNER'")
    boolean isOwner(int travelPlanId, int userId);

    @Query("SELECT CASE WHEN COUNT(tpu) > 0 THEN TRUE ELSE FALSE END FROM TravelPlanUserEntity tpu WHERE tpu.id.travelPlanEntity.id = :travelPlanId AND tpu.id.userEntity.id = :userId")
    boolean isMember(int travelPlanId, int userId);

    @Query("SELECT CASE WHEN COUNT(tpu) > 0 THEN TRUE ELSE FALSE END FROM TravelPlanUserEntity tpu WHERE tpu.id.travelPlanEntity.id = :travelPlanId AND tpu.id.userEntity.id = :userId AND (tpu.role = 'ADMIN' OR tpu.role = 'OWNER')")
    boolean isAdmin(int travelPlanId, int userId);

    @Query("SELECT tpu.role FROM TravelPlanUserEntity tpu WHERE tpu.id.travelPlanEntity.id = :travelPlanId AND tpu.id.userEntity.id = :userId")
    String getRole(int travelPlanId, int userId);

    @Modifying
    @Query("UPDATE TravelPlanUserEntity tpu SET tpu.role = :role WHERE tpu.id.travelPlanEntity.id = :travelPlanId AND tpu.id.userEntity.id = :userId")
    void updateRole(int travelPlanId, int userId, String role);

    @Query("SELECT tpu FROM TravelPlanUserEntity tpu WHERE tpu.id.travelPlanEntity.id = :travelPlanId AND tpu.id.userEntity.id = :userId")
    Optional<TravelPlanUserEntity> findByTravelPlanIdAndUserId(int travelPlanId, int userId);

    @Modifying
    @Query("DELETE FROM TravelPlanUserEntity tpu WHERE tpu.id.travelPlanEntity.id = :travelPlanId AND tpu.id.userEntity.id = :userId")
    void deleteByTravelPlanIdAndUserId(int travelPlanId, int userId);

    // get owner
    @Query("SELECT tpu FROM TravelPlanUserEntity tpu WHERE tpu.id.travelPlanEntity.id = :travelPlanId AND tpu.role = 'OWNER'")
    Optional<TravelPlanUserEntity> findOwnerByTravelPlanId(int travelPlanId);
}
