package com.travelbuddy.notification.rest;

import com.travelbuddy.common.constants.PaginationLimitConstants;
import com.travelbuddy.common.constants.ReactionTypeEnum;
import com.travelbuddy.common.mapper.PageMapper;
import com.travelbuddy.common.paging.PageDto;
import com.travelbuddy.common.utils.RequestUtils;
import com.travelbuddy.notification.NotiEntityTypeEnum;
import com.travelbuddy.notification.NotificationTypeEnum;
import com.travelbuddy.persistence.domain.dto.site.SiteBasicInfoRspnDto;
import com.travelbuddy.persistence.domain.entity.*;
import com.travelbuddy.persistence.repository.*;
import com.travelbuddy.site.user.SiteService;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {
    private final NotificationRepository notificationRepository;
    private final RequestUtils requestUtils;
    private final PageMapper pageMapper;
    private final SiteRepository siteRepository;
    private final SiteReviewRepository siteReviewRepository;
    private final UserRepository userRepository;
    private final SiteService siteService;
    private final SiteReactionRepository siteReactionRepository;
    private final ReviewReactionRepository reviewReactionRepository;
    private final TravelPlanRepository travelPlanRepository;

    @Override
    public PageDto<Object> getNotifications(int page) {
        int userId = requestUtils.getUserIdCurrentRequest();
        Pageable pageable = PageRequest.of(page - 1, PaginationLimitConstants.NOTIFICATION_LIMIT, Sort.by(Sort.Order.desc("lastUpdated")));
        Page<NotificationEntity> notifications = notificationRepository.findAllByUserId(userId, pageable);

        Page<Object> jsonPage = new PageImpl<>(
                notifications.stream().map(this::toJsonObject).filter(Objects::nonNull).collect(Collectors.toList()),
                pageable,
                notifications.getTotalElements()
        );

        return pageMapper.toPageDto(jsonPage);
    }

    @Override
    public void markAsRead(int notificationId) {
        NotificationEntity notification = notificationRepository.findById(notificationId).orElse(null);
        if (notification == null) {
            return;
        }
        notification.setRead(true);
        notificationRepository.save(notification);
    }

    private Map<String, Object> toJsonObject(NotificationEntity notification) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("notificationId", notification.getId());
        int type = notification.getType();
        Integer entityType = notification.getEntityType();
        Integer entityId = notification.getEntityId();

        try {
        switch (NotificationTypeEnum.valueOf(type)) {
            case SITE_COMMENT: {
                if (entityType != NotiEntityTypeEnum.SITE.getType()) {
                    break;
                }
                SiteReviewEntity lastReview = siteReviewRepository.findFirstBySiteIdOrderByCreatedAtDesc(entityId);
                if (lastReview == null) {
                    break;
                }
                SiteBasicInfoRspnDto siteBasicInfoRspnDto = siteService.getSiteBasicRepresentation(entityId);
                jsonObject.put("userId", lastReview.getUserId());
                jsonObject.put("userName", lastReview.getUserEntity().getFullName());
                jsonObject.put("userImageUrl", lastReview.getUserEntity().getAvatar() == null ? null : lastReview.getUserEntity().getAvatar());
                jsonObject.put("message", " Đã bình luận về địa điểm ");
                jsonObject.put("siteId", entityId);
                jsonObject.put("siteName", siteBasicInfoRspnDto.getSiteName());
                jsonObject.put("siteReview", lastReview.getComment());
                jsonObject.put("countOther", siteReviewRepository.countBySiteId(entityId) - 1);
                String optFullMessage = jsonObject.getInt("countOther") > 0
                        ? " Và " + String.valueOf(jsonObject.getInt("countOther")) + " người khác"
                        : "";
                String fullMessage = jsonObject.getString("userName") + optFullMessage + jsonObject.getString("message")
                        + jsonObject.getString("siteName") + " : " + jsonObject.getString("siteReview");
                jsonObject.put("fullMessage", fullMessage);
                jsonObject.put("createdAt", lastReview.getCreatedAt().toString());
                break;
            }
            case SITE_REACTION: {
                if (entityType != NotiEntityTypeEnum.SITE.getType()) {
                    break;
                }
                SiteReactionEntity lastSiteReaction = siteReactionRepository.findFirstBySiteIdOrderByCreatedAtDesc(entityId, ReactionTypeEnum.LIKE.name());
                if (lastSiteReaction == null) {
                    break;
                }
                SiteBasicInfoRspnDto siteBasicInfoRspnDto = siteService.getSiteBasicRepresentation(entityId);
                jsonObject.put("userId", lastSiteReaction.getUserId());
                jsonObject.put("userName", lastSiteReaction.getUserEntity().getFullName());
                jsonObject.put("userImageUrl", lastSiteReaction.getUserEntity().getAvatar() == null ? null : lastSiteReaction.getUserEntity().getAvatar());
                jsonObject.put("message", " Đã thích địa điểm ");
                jsonObject.put("siteId", entityId);
                jsonObject.put("siteName", siteBasicInfoRspnDto.getSiteName());
                jsonObject.put("countOther", siteReactionRepository.countBySiteIdAndReactionType(entityId, ReactionTypeEnum.LIKE.name()) - 1);
                String optFullMessage = jsonObject.getInt("countOther") > 0
                        ? " Và " + String.valueOf(jsonObject.getInt("countOther")) + " người khác"
                        : "";
                String fullMessage = jsonObject.getString("userName") + optFullMessage + jsonObject.getString("message")
                        + jsonObject.getString("siteName") + " của bạn";
                jsonObject.put("fullMessage", fullMessage);
                jsonObject.put("createdAt", lastSiteReaction.getCreatedAt().toString());
                break;
            }
            case REVIEW_REACTION: {
                if (entityType != NotiEntityTypeEnum.SITE_REVIEW.getType()) {
                    break;
                }
                ReviewReactionEntity lastReviewReaction = reviewReactionRepository.findFirstBySiteReviewIdOrderByCreatedAtDesc(entityId, ReactionTypeEnum.LIKE.name());
                if (lastReviewReaction == null) {
                    break;
                }
                SiteReviewEntity siteReview = siteReviewRepository.findById(entityId).orElse(null);
                if (siteReview == null) {
                    break;
                }
                SiteBasicInfoRspnDto siteBasicInfoRspnDto = siteService.getSiteBasicRepresentation(siteReview.getSiteId());
                jsonObject.put("userId", lastReviewReaction.getUserId());
                jsonObject.put("userName", lastReviewReaction.getUserEntity().getFullName());
                jsonObject.put("userImageUrl", lastReviewReaction.getUserEntity().getAvatar() == null ? null : lastReviewReaction.getUserEntity().getAvatar());
                jsonObject.put("message", " Đã thích bài đánh giá về địa điểm ");
                jsonObject.put("siteId", siteReview.getSiteId());
                jsonObject.put("siteName", siteBasicInfoRspnDto.getSiteName());
                jsonObject.put("siteReview", siteReview.getComment());
                jsonObject.put("countOther", reviewReactionRepository.countByReviewIdAndReactionType(entityId, ReactionTypeEnum.LIKE.name()) - 1);
                String optFullMessage = jsonObject.getInt("countOther") > 0
                        ? " Và " + String.valueOf(jsonObject.getInt("countOther")) + " người khác"
                        : "";
                String fullMessage = jsonObject.getString("userName") + optFullMessage + jsonObject.getString("message")
                        + jsonObject.getString("siteName") + " : " + jsonObject.getString("siteReview");
                jsonObject.put("fullMessage", fullMessage);
                jsonObject.put("createdAt", lastReviewReaction.getCreatedAt().toString());
                break;
            }
            case PLAN:
            case PLAN_ADD:
            case PLAN_UPDATE:
            case PLAN_DELETE: {
                if (entityType != NotiEntityTypeEnum.TRAVEL_PLAN.getType()) {
                    break;
                }
                TravelPlanEntity travelPlan = travelPlanRepository.findById(entityId).orElse(null);
                if (travelPlan == null) {
                    break;
                }
                JSONObject content = new JSONObject(notification.getContent());
                int userId = content.getInt("userId");
                UserEntity user = userRepository.findById(userId).orElse(null);
                if (user == null) {
                    break;
                }
                jsonObject.put("userId", userId);
                jsonObject.put("userName", user.getFullName());
                jsonObject.put("userImageUrl", user.getAvatar() == null ? null : user.getAvatar());
                jsonObject.put("planId", entityId);
                jsonObject.put("planName", travelPlan.getName());
                jsonObject.put("content", content);
                String fullMessage = jsonObject.getString("userName") + " đã thay đổi kế hoạch của " + jsonObject.getString("planName");
                jsonObject.put("fullMessage", fullMessage);
                jsonObject.put("createdAt", notification.getLastUpdated().toString());
            }
            case SITE_APPROVE: {
                if (entityType != NotiEntityTypeEnum.SITE.getType()) {
                    break;
                }
                SiteBasicInfoRspnDto siteBasicInfoRspnDto = siteService.getSiteBasicRepresentation(entityId);
                jsonObject.put("siteId", entityId);
                jsonObject.put("siteName", siteBasicInfoRspnDto.getSiteName());
                jsonObject.put("message", "Hệ thống đã phê duyệt thông tin địa điểm của bạn");
                String fullMessage = jsonObject.getString("message") + " : " + jsonObject.getString("siteName");
                jsonObject.put("fullMessage", fullMessage);
                jsonObject.put("createdAt", notification.getLastUpdated().toString());
                break;
            }
            case SITE_BAN: {
                if (entityType != NotiEntityTypeEnum.SITE.getType()) {
                    break;
                }
                SiteBasicInfoRspnDto siteBasicInfoRspnDto = siteService.getSiteBasicRepresentation(entityId);
                jsonObject.put("siteId", entityId);
                jsonObject.put("siteName", siteBasicInfoRspnDto.getSiteName());
                jsonObject.put("message", "Hệ thống đã khóa địa điểm của bạn");
                String fullMessage = jsonObject.getString("message") + " : " + jsonObject.getString("siteName");
                jsonObject.put("fullMessage", fullMessage);
                jsonObject.put("createdAt", notification.getLastUpdated().toString());
                break;
            }
            case SITE_UNBAN: {
                if (entityType != NotiEntityTypeEnum.SITE.getType()) {
                    break;
                }
                SiteBasicInfoRspnDto siteBasicInfoRspnDto = siteService.getSiteBasicRepresentation(entityId);
                jsonObject.put("siteId", entityId);
                jsonObject.put("siteName", siteBasicInfoRspnDto.getSiteName());
                jsonObject.put("message", "Hệ thống đã mở khóa địa điểm của bạn");
                String fullMessage = jsonObject.getString("message") + " : " + jsonObject.getString("siteName");
                jsonObject.put("fullMessage", fullMessage);
                jsonObject.put("createdAt", notification.getLastUpdated().toString());
                break;
            }
            case REVIEW_BAN: {
                if (entityType != NotiEntityTypeEnum.SITE_REVIEW.getType()) {
                    break;
                }
                SiteReviewEntity siteReview = siteReviewRepository.findById(entityId).orElse(null);
                if (siteReview == null) {
                    break;
                }
                SiteBasicInfoRspnDto siteBasicInfoRspnDto = siteService.getSiteBasicRepresentation(siteReview.getSiteId());
                jsonObject.put("siteId", siteReview.getSiteId());
                jsonObject.put("siteName", siteBasicInfoRspnDto.getSiteName());
                jsonObject.put("siteReviewId", entityId);
                jsonObject.put("siteReview", siteReview.getComment());
                jsonObject.put("message", "Hệ thống đã khóa bài đánh giá của bạn");
                String fullMessage = jsonObject.getString("message") + " : " + jsonObject.getString("siteName") + " : " + jsonObject.getString("siteReview");
                jsonObject.put("fullMessage", fullMessage);
                jsonObject.put("createdAt", notification.getLastUpdated().toString());
                break;
            }
            case REVIEW_UNBAN: {
                if (entityType != NotiEntityTypeEnum.SITE_REVIEW.getType()) {
                    break;
                }
                SiteReviewEntity siteReview = siteReviewRepository.findById(entityId).orElse(null);
                if (siteReview == null) {
                    break;
                }
                SiteBasicInfoRspnDto siteBasicInfoRspnDto = siteService.getSiteBasicRepresentation(siteReview.getSiteId());
                jsonObject.put("siteId", siteReview.getSiteId());
                jsonObject.put("siteName", siteBasicInfoRspnDto.getSiteName());
                jsonObject.put("siteReviewId", entityId);
                jsonObject.put("siteReview", siteReview.getComment());
                jsonObject.put("message", "Hệ thống đã mở khóa bài đánh giá của bạn");
                String fullMessage = jsonObject.getString("message") + " : " + jsonObject.getString("siteName") + " : " + jsonObject.getString("siteReview");
                jsonObject.put("fullMessage", fullMessage);
                jsonObject.put("createdAt", notification.getLastUpdated().toString());
                break;
            }
            case USER_BAN: {
                if (entityType != NotiEntityTypeEnum.USER.getType()) {
                    break;
                }
                UserEntity user = userRepository.findById(entityId).orElse(null);
                if (user == null) {
                    break;
                }
                jsonObject.put("message", "Hệ thống đã khóa tài khoản của bạn");
                jsonObject.put("fullMessage", jsonObject.getString("message"));
                jsonObject.put("createdAt", notification.getLastUpdated().toString());
                break;
            }
            case USER_UNBAN: {
                if (entityType != NotiEntityTypeEnum.USER.getType()) {
                    break;
                }
                UserEntity user = userRepository.findById(entityId).orElse(null);
                if (user == null) {
                    break;
                }
                jsonObject.put("message", "Hệ thống đã mở khóa tài khoản của bạn");
                jsonObject.put("fullMessage", jsonObject.getString("message"));
                jsonObject.put("createdAt", notification.getLastUpdated().toString());
                break;
            }
            default:
                break;
        }
        } catch (Exception e) {
            return null;
        }
        if (jsonObject.isEmpty()) {
            return null;
        }
        System.out.println("OBJECT: " + jsonObject);
        return jsonObject.toMap();
    }
}
