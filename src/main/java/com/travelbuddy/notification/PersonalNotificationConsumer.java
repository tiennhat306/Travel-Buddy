package com.travelbuddy.notification;

import com.google.gson.JsonObject;
import com.travelbuddy.common.constants.ReactionTypeEnum;
import com.travelbuddy.common.exception.errorresponse.NotFoundException;
import com.travelbuddy.persistence.domain.dto.site.SiteBasicInfoRspnDto;
import com.travelbuddy.persistence.domain.entity.*;
import com.travelbuddy.persistence.repository.*;
import com.travelbuddy.site.user.SiteService;
import com.travelbuddy.siteapproval.admin.SiteApprovalService;
import com.travelbuddy.sitereviews.SiteReviewService;
import com.travelbuddy.siteversion.user.SiteVersionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.travelbuddy.notification.NotificationTypeEnum.*;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
@KafkaListener(topics = "notifications", groupId = "travel-buddy")
public class PersonalNotificationConsumer {
    private final SimpMessagingTemplate messagingTemplate;
    private final NotificationRepository notificationRepository;
    private final SiteRepository siteRepository;
    private final UserRepository userRepository;
    private final SiteService siteService;
    private final SiteReviewRepository siteReviewRepository;
    private final TravelPlanRepository travelPlanRepository;
    private final SiteReactionRepository siteReactionRepository;

    @KafkaHandler
    public void consume(String message) {
        // Parse message
        JSONObject payload = new JSONObject(message);

        int type = payload.getInt("type");
        int entityType = payload.getInt("entityType");
        int entityId = payload.getInt("entityId");

        handleNotification(type, entityType, entityId, payload);
    }

    private void sendNotification(int userId, String message) {
        String queue = "/queue/user-" + userId;
        System.out.println("Sending notification to " + queue + ": " + message);
        messagingTemplate.convertAndSend(queue, message);
    }

    private Integer saveNotification(int userId, int type, int entityType, int entityId, String content) {
        NotificationEntity notificationEntity = NotificationEntity.builder()
                .userId(userId)
                .type(type)
                .entityType(entityType)
                .entityId(entityId)
                .content(content)
                .build();
        return notificationRepository.save(notificationEntity).getId();
    }

    private void handleNotification(int type, int entityType, int entityId, JSONObject payload) {
        try {
            System.out.println("HEHE" + NotificationTypeEnum.valueOf(type) + "and" + NotificationTypeEnum.valueOf(type).equals(SITE_REACTION));
            switch (NotificationTypeEnum.valueOf(type)) {
                case SITE_COMMENT: {
                    int userId = payload.getInt("userId");
                    UserEntity userEntity = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));
                    if (entityType == NotiEntityTypeEnum.SITE.getType()) {
                        SiteEntity siteEntity = siteRepository.findById(entityId).orElseThrow(() -> new NotFoundException("Site not found"));
                        SiteReviewEntity siteReviewEntity = siteReviewRepository.findBySiteIdAndUserId(entityId, userId);
                        if (siteReviewEntity == null) {
                            throw new NotFoundException("Site review not found");
                        }
                        SiteBasicInfoRspnDto siteBasicInfoRspnDto = siteService.getSiteBasicRepresentation(entityId);
                        int destinationUserId = siteEntity.getOwnerId();
                        NotificationEntity notificationEntity = notificationRepository.findByUserIdAndTypeAndEntityTypeAndEntityId(destinationUserId, type, entityType, entityId);

                        if (notificationEntity == null) {
                            JSONArray userIds = new JSONArray();
                            userIds.put(userId);
                            JSONObject saveContentJson = new JSONObject();
                            saveContentJson.put("userIds", userIds);
                            int notificationId = saveNotification(destinationUserId, type, entityType, entityId, saveContentJson.toString());

                            JSONObject contentJson = new JSONObject();
                            contentJson.put("notificationId", notificationId);
                            contentJson.put("userId", userId);
                            contentJson.put("userName", userEntity.getFullName());
                            contentJson.put("userImageUrl", userEntity.getAvatar() != null ? userEntity.getAvatar().getUrl() : null);
                            contentJson.put("message", " Đã bình luận về địa điểm ");
                            contentJson.put("siteId", entityId);
                            contentJson.put("siteName", siteBasicInfoRspnDto.getSiteName());
                            contentJson.put("siteReview", siteReviewEntity.getComment());
                            String fullMessage = contentJson.getString("userName") + contentJson.getString("message")
                                    + contentJson.getString("siteName") + " : " + contentJson.getString("siteReview");
                            contentJson.put("fullMessage", fullMessage);
                            sendNotification(destinationUserId, contentJson.toString());
                        } else {
                            String content = notificationEntity.getContent();
                            JSONObject contentJson = new JSONObject(content);
                            JSONArray userIds = contentJson.optJSONArray("userIds");
                            if (userIds == null) {
                                userIds = new JSONArray();
                            }
                            if (!userIds.toList().contains(entityId)) {
                                userIds.put(entityId);
                                contentJson.put("userIds", userIds);
                                notificationEntity.setContent(contentJson.toString());
                                notificationRepository.save(notificationEntity);

                                JSONObject newContentJson = new JSONObject();
                                newContentJson.put("notificationId", notificationEntity.getId());
                                newContentJson.put("userId", userId);
                                newContentJson.put("userName", userEntity.getFullName());
                                newContentJson.put("userImageUrl", userEntity.getAvatar() != null ? userEntity.getAvatar().getUrl() : null);
                                newContentJson.put("message", " Đã bình luận về địa điểm ");
                                newContentJson.put("siteId", entityId);
                                newContentJson.put("siteName", siteBasicInfoRspnDto.getSiteName());
                                newContentJson.put("siteReview", siteReviewEntity.getComment());
                                newContentJson.put("countOther", siteEntity.getSiteReviewEntities().size() - 1);
                                String optFullMessage = newContentJson.getInt("countOther") > 0
                                        ? " Và " + String.valueOf(newContentJson.getInt("countOther")) + " người khác"
                                        : "";
                                String fullMessage = newContentJson.getString("userName") + optFullMessage +  newContentJson.getString("message")
                                        + newContentJson.getString("siteName") + " : " + newContentJson.getString("siteReview");
                                newContentJson.put("fullMessage", fullMessage);
                                sendNotification(destinationUserId, newContentJson.toString());
                            }
                        }
                    }
                    break;
                }
                case SITE_REACTION: {
                    int userId = payload.getInt("userId");
                    UserEntity userEntity = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));
                    if (entityType == NotiEntityTypeEnum.SITE.getType()) {
                        SiteEntity siteEntity = siteRepository.findById(entityId).orElseThrow(() -> new NotFoundException("Site not found"));
                        SiteBasicInfoRspnDto siteBasicInfoRspnDto = siteService.getSiteBasicRepresentation(entityId);
                        int destinationUserId = siteEntity.getOwnerId();
                        NotificationEntity notificationEntity = notificationRepository.findByUserIdAndTypeAndEntityTypeAndEntityId(destinationUserId, type, entityType, entityId);

                        if (notificationEntity == null) {
                            JSONArray userIds = new JSONArray();
                            userIds.put(userId);
                            JSONObject saveContentJson = new JSONObject();
                            saveContentJson.put("userIds", userIds);
                            int notificationId = saveNotification(destinationUserId, type, entityType, entityId, saveContentJson.toString());

                            JSONObject contentJson = new JSONObject();
                            contentJson.put("notificationId", notificationId);
                            contentJson.put("userId", userId);
                            contentJson.put("userName", userEntity.getFullName());
                            contentJson.put("userImageUrl", userEntity.getAvatar() != null ? userEntity.getAvatar().getUrl() : null);
                            contentJson.put("message", " Đã thích địa điểm ");
                            contentJson.put("siteId", entityId);
                            contentJson.put("siteName", siteBasicInfoRspnDto.getSiteName());
                            String fullMessage = contentJson.getString("userName") + contentJson.getString("message")
                                    + contentJson.getString("siteName") + " của bạn";
                            contentJson.put("fullMessage", fullMessage);
                            sendNotification(destinationUserId, contentJson.toString());
                        } else {
                            String content = notificationEntity.getContent();
                            JSONObject contentJson = new JSONObject(content);
                            JSONArray userIds = contentJson.optJSONArray("userIds");
                            if (userIds == null) {
                                userIds = new JSONArray();
                            }
                            if (!userIds.toList().contains(entityId)) {
                                userIds.put(entityId);
                                contentJson.put("userIds", userIds);
                                notificationEntity.setContent(contentJson.toString());
                                notificationRepository.save(notificationEntity);

                                JSONObject newContentJson = new JSONObject();
                                newContentJson.put("userId", userId);
                                newContentJson.put("userName", userEntity.getFullName());
                                newContentJson.put("userImageUrl", userEntity.getAvatar() != null ? userEntity.getAvatar().getUrl() : null);
                                newContentJson.put("message", " Đã thích địa điểm ");
                                newContentJson.put("siteId", entityId);
                                newContentJson.put("siteName", siteBasicInfoRspnDto.getSiteName());
                                newContentJson.put("countOther", siteReactionRepository.countBySiteIdAndReactionType(entityId, ReactionTypeEnum.LIKE.name()) - 1);
                                String optFullMessage = newContentJson.getInt("countOther") > 0
                                        ? " Và " + String.valueOf(newContentJson.getInt("countOther")) + " người khác"
                                        : "";
                                String fullMessage = newContentJson.getString("userName") + optFullMessage + newContentJson.getString("message")
                                        + newContentJson.getString("siteName") + " của bạn";
                                newContentJson.put("fullMessage", fullMessage);
                                sendNotification(destinationUserId, newContentJson.toString());
                            }
                        }
                    }
                    break;
                }
                case REVIEW_REACTION: {
                    int userId = payload.getInt("userId");
                    UserEntity userEntity = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));
                    if (entityType == NotiEntityTypeEnum.SITE_REVIEW.getType()) {
                        SiteReviewEntity siteReviewEntity = siteReviewRepository.findById(entityId).orElseThrow(() -> new NotFoundException("Site review not found"));
                        SiteBasicInfoRspnDto siteBasicInfoRspnDto = siteService.getSiteBasicRepresentation(siteReviewEntity.getSiteId());
                        int destinationUserId = siteReviewEntity.getUserId();
                        NotificationEntity notificationEntity = notificationRepository.findByUserIdAndTypeAndEntityTypeAndEntityId(destinationUserId, type, entityType, entityId);

                        if (notificationEntity == null) {
                            JSONArray userIds = new JSONArray();
                            userIds.put(userId);
                            JSONObject saveContentJson = new JSONObject();
                            saveContentJson.put("userIds", userIds);
                            int notificationId =  saveNotification(destinationUserId, type, entityType, entityId, saveContentJson.toString());

                            JSONObject contentJson = new JSONObject();
                            contentJson.put("notificationId", notificationId);
                            contentJson.put("userId", userId);
                            contentJson.put("userName", userEntity.getFullName());
                            contentJson.put("userImageUrl", userEntity.getAvatar() != null ? userEntity.getAvatar().getUrl() : null);
                            contentJson.put("message", " Đã thích bài đánh giá về địa điểm ");
                            contentJson.put("siteId", siteReviewEntity.getSiteId());
                            contentJson.put("siteName", siteBasicInfoRspnDto.getSiteName());
                            contentJson.put("siteReview", siteReviewEntity.getComment());
                            String fullMessage = contentJson.getString("userName") + contentJson.getString("message")
                                    + contentJson.getString("siteName") + " : " + contentJson.getString("siteReview");
                            contentJson.put("fullMessage", fullMessage);
                            sendNotification(destinationUserId, contentJson.toString());
                        } else {
                            String content = notificationEntity.getContent();
                            JSONObject contentJson = new JSONObject(content);
                            JSONArray userIds = contentJson.optJSONArray("userIds");
                            if (userIds == null) {
                                userIds = new JSONArray();
                            }
                            if (!userIds.toList().contains(entityId)) {
                                userIds.put(entityId);
                                contentJson.put("userIds", userIds);
                                notificationEntity.setContent(contentJson.toString());
                                notificationRepository.save(notificationEntity);

                                JSONObject newContentJson = new JSONObject();
                                newContentJson.put("userId", userId);
                                newContentJson.put("userName", userEntity.getFullName());
                                newContentJson.put("userImageUrl", userEntity.getAvatar() != null ? userEntity.getAvatar().getUrl() : null);
                                newContentJson.put("message", " Đã thích bài đánh giá về địa điểm");
                                newContentJson.put("siteId", siteReviewEntity.getSiteId());
                                newContentJson.put("siteName", siteBasicInfoRspnDto.getSiteName());
                                newContentJson.put("siteReview", siteReviewEntity.getComment());
                                newContentJson.put("countOther", siteReviewEntity.getReviewReactions().size() - 1);
                                String optFullMessage = newContentJson.getInt("countOther") > 0
                                        ? " Và " + String.valueOf(newContentJson.getInt("countOther")) + " người khác"
                                        : "";
                                String fullMessage = newContentJson.getString("userName") + optFullMessage + newContentJson.getString("message")
                                        + newContentJson.getString("siteName") + " : " + newContentJson.getString("siteReview");
                                newContentJson.put("fullMessage", fullMessage);
                                sendNotification(destinationUserId, newContentJson.toString());
                            }
                        }
                    }
                    break;
                }
                case PLAN:
                case PLAN_ADD:
                case PLAN_UPDATE:
                case PLAN_DELETE: {
                    int userId = payload.getInt("userId");
                    UserEntity userEntity = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));
                    if (entityType == NotiEntityTypeEnum.TRAVEL_PLAN.getType()) {
                        TravelPlanEntity travelPlanEntity = travelPlanRepository.findById(entityId).orElseThrow(() -> new NotFoundException("Travel plan not found"));
                        List<Integer> destinationUserIds = travelPlanRepository.findUserIdsById(entityId);

                        for (int destinationUserId : destinationUserIds) {
                            String content = payload.getString("content");
                            int notificationId = saveNotification(destinationUserId, type, entityType, entityId, content);

                            JSONObject contentJson = new JSONObject();
                            contentJson.put("notificationId", notificationId);
                            contentJson.put("userId", userId);
                            contentJson.put("userName", userEntity.getFullName());
                            contentJson.put("userImageUrl", userEntity.getAvatar() != null ? userEntity.getAvatar().getUrl() : null);
                            contentJson.put("planId", entityId);
                            contentJson.put("planName",travelPlanEntity.getName());
                            contentJson.put("content", content);
                            String fullMessage = contentJson.getString("userName") + " đã thay đổi kế hoạch của " + contentJson.getString("planName") + " : " + contentJson.getString("content");
                            contentJson.put("fullMessage", fullMessage);
                            sendNotification(destinationUserId, contentJson.toString());
                        }
                    }
                    break;
                }
                case SITE_BAN: {
                    if (entityType == NotiEntityTypeEnum.SITE.getType()) {
                        SiteBasicInfoRspnDto siteBasicInfoRspnDto = siteService.getSiteBasicRepresentation(entityId);
                        int destinationUserId = siteBasicInfoRspnDto.getOwnerId();
                        int notificationId = saveNotification(destinationUserId, type, entityType, entityId, null);

                        JSONObject contentJson = new JSONObject();
                        contentJson.put("notificationId", notificationId);
                        contentJson.put("siteId", entityId);
                        contentJson.put("siteName", siteBasicInfoRspnDto.getSiteName());
                        contentJson.put("message", "Hệ thống đã khóa địa điểm của bạn");
                        String fullMessage = contentJson.getString("message") + " " + contentJson.getString("siteName");
                        contentJson.put("fullMessage", fullMessage);
                        sendNotification(destinationUserId, contentJson.toString());
                    }
                    break;
                }
                case SITE_UNBAN: {
                    if (entityType == NotiEntityTypeEnum.SITE.getType()) {
                        SiteBasicInfoRspnDto siteBasicInfoRspnDto = siteService.getSiteBasicRepresentation(entityId);
                        int destinationUserId = siteBasicInfoRspnDto.getOwnerId();
                        int notificationId = saveNotification(destinationUserId, type, entityType, entityId, null);

                        JSONObject contentJson = new JSONObject();
                        contentJson.put("notificationId", notificationId);
                        contentJson.put("siteId", entityId);
                        contentJson.put("siteName", siteBasicInfoRspnDto.getSiteName());
                        contentJson.put("message", "Hệ thống đã mở khóa địa điểm của bạn");
                        String fullMessage = contentJson.getString("message") + " " + contentJson.getString("siteName");
                        contentJson.put("fullMessage", fullMessage);
                        sendNotification(destinationUserId, contentJson.toString());
                    }
                    break;
                }
                case REVIEW_BAN: {
                    if (entityType == NotiEntityTypeEnum.SITE_REVIEW.getType()) {
                        SiteReviewEntity siteReviewEntity = siteReviewRepository.findById(entityId).orElseThrow(() -> new NotFoundException("Site review not found"));
                        SiteBasicInfoRspnDto siteBasicInfoRspnDto = siteService.getSiteBasicRepresentation(siteReviewEntity.getSiteId());
                        int destinationUserId = siteReviewEntity.getUserId();
                        int notificationId = saveNotification(destinationUserId, type, entityType, entityId, null);

                        JSONObject contentJson = new JSONObject();
                        contentJson.put("notificationId", notificationId);
                        contentJson.put("siteId", siteReviewEntity.getSiteId());
                        contentJson.put("siteName", siteBasicInfoRspnDto.getSiteName());
                        contentJson.put("siteReviewId", entityId);
                        contentJson.put("siteReview", siteReviewEntity.getComment());
                        contentJson.put("message", "Hệ thống đã khóa bài đánh giá của bạn");
                        String fullMessage = contentJson.getString("message") + " " + contentJson.getString("siteName") + " : " + contentJson.getString("siteReview");
                        contentJson.put("fullMessage", fullMessage);
                        sendNotification(destinationUserId, contentJson.toString());
                    }
                    break;
                }
                case REVIEW_UNBAN: {
                    if (entityType == NotiEntityTypeEnum.SITE_REVIEW.getType()) {
                        SiteReviewEntity siteReviewEntity = siteReviewRepository.findById(entityId).orElseThrow(() -> new NotFoundException("Site review not found"));
                        SiteBasicInfoRspnDto siteBasicInfoRspnDto = siteService.getSiteBasicRepresentation(siteReviewEntity.getSiteId());
                        int destinationUserId = siteReviewEntity.getUserId();
                        int notificationId = saveNotification(destinationUserId, type, entityType, entityId, null);

                        JSONObject contentJson = new JSONObject();
                        contentJson.put("notificationId", notificationId);
                        contentJson.put("siteId", siteReviewEntity.getSiteId());
                        contentJson.put("siteName", siteBasicInfoRspnDto.getSiteName());
                        contentJson.put("siteReviewId", entityId);
                        contentJson.put("siteReview", siteReviewEntity.getComment());
                        contentJson.put("message", "Hệ thống đã mở khóa bài đánh giá của bạn");
                        String fullMessage = contentJson.getString("message") + " " + contentJson.getString("siteName") + " : " + contentJson.getString("siteReview");
                        contentJson.put("fullMessage", fullMessage);
                        sendNotification(destinationUserId, contentJson.toString());
                    }
                    break;
                }
                case USER_BAN: {
                    if (entityType == NotiEntityTypeEnum.USER.getType()) {
                        userRepository.findById(entityId).orElseThrow(() -> new NotFoundException("User not found"));
                        int notificationId = saveNotification(entityId, type, entityType, entityId, null);

                        JSONObject contentJson = new JSONObject();
                        contentJson.put("notificationId", notificationId);
                        contentJson.put("message", "Hệ thống đã khóa tài khoản của bạn");
//                        sendNotification(entityId, contentJson.toString());
                    }
                    break;
                }
                case USER_UNBAN: {
                    if (entityType == NotiEntityTypeEnum.USER.getType()) {
                        userRepository.findById(entityId).orElseThrow(() -> new NotFoundException("User not found"));
                        int notificationId = saveNotification(entityId, type, entityType, entityId, null);

                        JSONObject contentJson = new JSONObject();
                        contentJson.put("notificationId", notificationId);
                        contentJson.put("message", "Hệ thống đã mở khóa tài khoản của bạn");
//                        sendNotification(entityId, contentJson.toString());
                    }
                    break;
                }
                default:
                    throw new IllegalArgumentException("Invalid notification type: " + type);
            }
        } catch (Exception e) {
            log.error("Failed to handle notification", e);
            return;
        }
    }
}