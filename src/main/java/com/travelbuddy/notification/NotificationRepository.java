package com.travelbuddy.notification;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface NotificationRepository extends JpaRepository<NotificationEntity, Long> {
    Page<NotificationEntity> findByUserIdOrderByLastUpdatedDesc(Integer userId, Pageable pageable);

    @Query
    NotificationEntity findByUserIdAndTypeAndEntityTypeAndEntityId(int userId, int type, int entityType, int entityId);
}