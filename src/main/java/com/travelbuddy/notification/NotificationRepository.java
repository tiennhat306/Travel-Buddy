package com.travelbuddy.notification;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface NotificationRepository extends JpaRepository<NotificationEntity, Integer> {
    @Query("SELECT n FROM NotificationEntity n WHERE n.userId = ?1 AND n.type = ?2 AND n.entityType = ?3 AND n.entityId = ?4")
    NotificationEntity findByUserIdAndTypeAndEntityTypeAndEntityId(int userId, int type, int entityType, int entityId);

    Page<NotificationEntity> findAllByUserId(int userId, Pageable pageable);
}