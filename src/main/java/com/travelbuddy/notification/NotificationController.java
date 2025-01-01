package com.travelbuddy.notification;

import com.travelbuddy.common.paging.PageDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notification")
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationService notificationService;

    @GetMapping
    public ResponseEntity<Object> getNotifications(@RequestParam(name = "page", required = false, defaultValue = "1") int page) {
        PageDto<Object> notifications = notificationService.getNotifications(page);
        return ResponseEntity.ok(notifications);
    }

    @PutMapping("/{notificationId}")
    public ResponseEntity<Void> markAsRead(@PathVariable int notificationId) {
        notificationService.markAsRead(notificationId);
        return ResponseEntity.ok().build();
    }
}
