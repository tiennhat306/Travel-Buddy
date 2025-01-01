package com.travelbuddy.notification;

import com.travelbuddy.common.paging.PageDto;

public interface NotificationService {
    PageDto<Object> getNotifications(int page);

    void markAsRead(int notificationId);
}
