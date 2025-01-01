package com.travelbuddy.notification;

import com.google.gson.JsonObject;
import com.travelbuddy.common.exception.errorresponse.NotFoundException;
import com.travelbuddy.persistence.domain.entity.SiteEntity;
import com.travelbuddy.persistence.domain.entity.SiteVersionEntity;
import com.travelbuddy.persistence.domain.entity.UserEntity;
import com.travelbuddy.persistence.repository.SiteRepository;
import com.travelbuddy.persistence.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@KafkaListener(topics = "notifications", groupId = "travel-buddy")
public class PersonalNotificationConsumer {
    private final SimpMessagingTemplate messagingTemplate;
    private final NotificationRepository notificationRepository;
    private final SiteRepository siteRepository;
//    private final UserRepository userRepository;
//    private final

    @KafkaHandler
    public void consume(String message) {
        // Parse message
        JSONObject payload = new JSONObject(message);
        int userId = payload.getInt("userId");

//        UserEntity userEntity = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));

        int type = payload.getInt("type");

        int entityType = payload.getInt("entityType");
        int entityId = payload.getInt("entityId");
//        String content = payload.getString("content");

//        handleNotification(type, entityType, entityId, userEntity);

        NotificationEntity notificationEntity = NotificationEntity.builder()
                .userId(userId)
                .type(type)
//                .content(content)
                .build();

        // Send via WebSocket
        String queue = "/queue/user-" + userId;
        messagingTemplate.convertAndSend(queue, payload.toString());
    }

    private void handleNotification(int type, int entityType, int entityId, UserEntity userEntity) {
        switch (NotificationTypeEnum.valueOf(type)) {
            case SITE_COMMENT: {
                if (entityType == NotiEntityTypeEnum.SITE.getType()) {
                    // Handle site comment notification
                    SiteEntity siteEntity = siteRepository.findById(entityId).orElseThrow(() -> new NotFoundException("Site not found"));
//                    SiteVersionEntity siteVersionEntity = siteEntity.getLatestVersion();
                    int destinationUserId = siteEntity.getOwnerId();
                    NotificationEntity notificationEntity = notificationRepository.findByUserIdAndTypeAndEntityTypeAndEntityId(destinationUserId, type, entityType, entityId);

                    if (notificationEntity == null) {
                        String content = String.format("%s đã bình luận về bài viết %s", userEntity.getFullName(), siteEntity.toString());
                        notificationEntity = NotificationEntity.builder()
                                .userId(destinationUserId)
                                .type(type)
                                .entityType(entityType)
                                .entityId(entityId)
                                .build();
                        notificationRepository.save(notificationEntity);
                    }
                }
            }
            case SITE_REACTION:
            case REVIEW_REACTION:
            case PLAN:
            case PLAN_ADD:
            case PLAN_UPDATE:
            case PLAN_DELETE:
            case SITE_BAN:
            case SITE_UNBAN:
            case REVIEW_BAN:
            case REVIEW_UNBAN:
            case USER_BAN:
            case USER_UNBAN:
                break;
            default:
                throw new IllegalArgumentException("Invalid notification type: " + type);
        }
    }
}