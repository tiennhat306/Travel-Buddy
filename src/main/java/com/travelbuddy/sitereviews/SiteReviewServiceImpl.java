package com.travelbuddy.sitereviews;

import com.travelbuddy.common.constants.MediaTypeEnum;
import com.travelbuddy.common.constants.ReactionTypeEnum;
import com.travelbuddy.common.exception.errorresponse.DataAlreadyExistsException;
import com.travelbuddy.common.exception.errorresponse.ForbiddenException;
import com.travelbuddy.common.exception.errorresponse.NotFoundException;
import com.travelbuddy.common.mapper.PageMapper;
import com.travelbuddy.common.paging.PageDto;
import com.travelbuddy.common.utils.RequestUtils;
import com.travelbuddy.mapper.SiteReviewMapper;
import com.travelbuddy.notification.NotiEntityTypeEnum;
import com.travelbuddy.notification.NotificationProducer;
import com.travelbuddy.notification.NotificationTypeEnum;
import com.travelbuddy.persistence.domain.dto.site.SiteBasicInfoRspnDto;
import com.travelbuddy.persistence.domain.dto.sitereview.*;
import com.travelbuddy.persistence.domain.entity.FileEntity;
import com.travelbuddy.persistence.domain.entity.ReviewMediaEntity;
import com.travelbuddy.persistence.domain.entity.ReviewReactionEntity;
import com.travelbuddy.persistence.domain.entity.SiteReviewEntity;
import com.travelbuddy.persistence.repository.ReviewMediaRepository;
import com.travelbuddy.persistence.repository.ReviewReactionRepository;
import com.travelbuddy.persistence.repository.SiteReviewRepository;
import com.travelbuddy.site.user.SiteService;
import com.travelbuddy.upload.cloud.StorageExecutorService;
import com.travelbuddy.upload.cloud.dto.FileRspnDto;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.travelbuddy.common.constants.PaginationLimitConstants.*;

@Service
@RequiredArgsConstructor
@Transactional
public class SiteReviewServiceImpl implements SiteReviewService {
    private final SiteReviewRepository siteReviewRepository;
    private final PageMapper pageMapper;
    private final SiteReviewMapper siteReviewMapper;
    private final SiteReviewSpecifications siteReviewSpecifications;
    private final ReviewMediaRepository reviewMediaRepository;
    private final StorageExecutorService storageExecutorService;
    private final ReviewReactionRepository reviewReactionRepository;
    private final RequestUtils requestUtils;
    private final SiteService siteService;
    private final NotificationProducer notificationProducer;

    @Override
    public void createSiteReview(SiteReviewCreateRqstDto siteReviewCreateRqstDto) {
        int userId = requestUtils.getUserIdCurrentRequest();

        boolean isReviewExists = siteReviewRepository.existsBySiteIdAndUserId(siteReviewCreateRqstDto.getSiteId(), userId);

        if (isReviewExists) {
            throw new DataAlreadyExistsException("You have already reviewed this site");
        }

        SiteReviewEntity siteReviewEntity = SiteReviewEntity.builder()
                .siteId(siteReviewCreateRqstDto.getSiteId())
                .userId(userId)
                .comment(siteReviewCreateRqstDto.getComment())
                .generalRating(siteReviewCreateRqstDto.getGeneralRating())
                .arrivalDate(siteReviewCreateRqstDto.getArrivalDate())
                .build();

        siteReviewEntity = siteReviewRepository.saveAndFlush(siteReviewEntity);

        List<ReviewMediaEntity> reviewMediaEntities = new ArrayList<>();
        for (FileRspnDto fileRspnDto : siteReviewCreateRqstDto.getMedias()) {
            reviewMediaEntities.add(ReviewMediaEntity.builder()
                    .media(FileEntity.builder()
                            .id(fileRspnDto.getId())
                            .url(fileRspnDto.getUrl())
                            .build())
                    .mediaType(fileRspnDto.getUrl().contains("video") ? MediaTypeEnum.VIDEO.name() : MediaTypeEnum.IMAGE.name())
                    .review(siteReviewEntity)
                    .build());
        }

        reviewMediaRepository.saveAll(reviewMediaEntities);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("userId", userId);
        jsonObject.put("type", NotificationTypeEnum.SITE_COMMENT.getType());
        jsonObject.put("entityType", NotiEntityTypeEnum.SITE.getType());
        jsonObject.put("entityId", siteReviewEntity.getSiteId());
        notificationProducer.sendNotification("notifications", jsonObject.toString());
    }

    @Override
    public PageDto<SiteReviewRspnDto> getAllSiteReviews(int siteId, int page) {
        Specification<SiteReviewEntity> specification = siteReviewSpecifications.customScoreBySiteId((long) siteId);

        Pageable pageable = PageRequest.of(page - 1, SITE_REVIEW_LIMIT);
        Page<SiteReviewEntity> siteReviews = siteReviewRepository.findAll(specification, pageable);

        int userId = requestUtils.getUserIdCurrentRequest();

        return pageMapper.toPageDto(siteReviews.map(siteReviewEntity -> siteReviewMapper.siteReviewEntityToSiteReviewRspnDto(siteReviewEntity, userId)));
    }

    @Override
    public void updateSiteReview(int siteReviewId, SiteReviewUpdateRqstDto siteReviewUpdateRqstDto) {
        SiteReviewEntity siteReviewEntity = siteReviewRepository.findById(siteReviewId)
                .orElseThrow(() -> new NotFoundException("Site review not found"));

        int userId = requestUtils.getUserIdCurrentRequest();
        if (siteReviewEntity.getUserId() != userId) {
            throw new ForbiddenException("You are not allowed to update this review");
        }

        List<String> mediaIdsToDelete = new ArrayList<>();

        siteReviewEntity.getReviewMedias().removeIf(reviewMediaEntity -> {
            if (!siteReviewUpdateRqstDto.getMediaIds().contains(reviewMediaEntity.getId())) {
                mediaIdsToDelete.add(reviewMediaEntity.getMedia().getId());
                return true;
            }
            return false;
        });

        storageExecutorService.deleteFiles(mediaIdsToDelete);

        List<ReviewMediaEntity> reviewMediaEntities = new ArrayList<>();
        for (FileRspnDto fileRspnDto : siteReviewUpdateRqstDto.getNewMedias()) {
            reviewMediaEntities.add(ReviewMediaEntity.builder()
                    .media(FileEntity.builder()
                            .id(fileRspnDto.getId())
                            .url(fileRspnDto.getUrl())
                            .build())
                    .mediaType(fileRspnDto.getUrl().contains("video") ? MediaTypeEnum.VIDEO.name() : MediaTypeEnum.IMAGE.name())
                    .review(siteReviewEntity)
                    .build());
        }

        siteReviewEntity.setComment(siteReviewUpdateRqstDto.getComment());
        siteReviewEntity.setGeneralRating(siteReviewUpdateRqstDto.getGeneralRating());
        siteReviewEntity.setArrivalDate(siteReviewUpdateRqstDto.getArrivalDate());
        // To update existing reviewMedias, we need to clear the old list and add the new list
        siteReviewEntity.getReviewMedias().addAll(reviewMediaEntities);

        siteReviewRepository.save(siteReviewEntity);
    }

    @Override
    public void deleteSiteReview(int reviewId) {
        SiteReviewEntity siteReviewEntity = siteReviewRepository.findById(reviewId)
                .orElseThrow(() -> new NotFoundException("Site review not found"));

        int userId = requestUtils.getUserIdCurrentRequest();
        if (siteReviewEntity.getUserId() != userId) {
            throw new ForbiddenException("You are not allowed to delete this review");
        }

        List<String> mediaIds = new ArrayList<>();
        siteReviewEntity.getReviewMedias().forEach(reviewMediaEntity -> mediaIds.add(reviewMediaEntity.getMedia().getId()));

        mediaIds.forEach(storageExecutorService::deleteFile);

//        reviewMediaRepository.deleteByReviewId(siteReviewEntity.getId());
        siteReviewRepository.delete(siteReviewEntity);
    }

    @Override
    public SiteReviewDetailRspnDto getSiteReviewById(int reviewId) {
        SiteReviewEntity siteReviewEntity = siteReviewRepository.findById(reviewId)
                .orElseThrow(() -> new NotFoundException("Site review not found"));

        return siteReviewMapper.siteReviewEntityToSiteReviewDetailRspnDto(siteReviewEntity);
    }

    @Override
    public void likeSiteReview(int reviewId) {
        int userId = requestUtils.getUserIdCurrentRequest();

        siteReviewRepository.findById(reviewId)
                .orElseThrow(() -> new NotFoundException("Site review not found"));

        Optional<ReviewReactionEntity> reviewReactionEntityOpt = reviewReactionRepository.findByUserIdAndReviewId(userId, reviewId);

        ReviewReactionEntity reviewReactionEntity;
        if (reviewReactionEntityOpt.isPresent()) {
            reviewReactionEntity = reviewReactionEntityOpt.get();
            if (ReactionTypeEnum.LIKE.name().equals(reviewReactionEntity.getReactionType())) {
                reviewReactionEntity.setReactionType(null);
            } else {
                reviewReactionEntity.setReactionType(ReactionTypeEnum.LIKE.name());
            }
        } else {
            reviewReactionEntity = ReviewReactionEntity.builder()
                    .userId(userId)
                    .reviewId(reviewId)
                    .reactionType(ReactionTypeEnum.LIKE.name())
                    .build();
        }

        reviewReactionRepository.save(reviewReactionEntity);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("userId", userId);
        jsonObject.put("type", NotificationTypeEnum.REVIEW_REACTION.getType());
        jsonObject.put("entityType", NotiEntityTypeEnum.SITE_REVIEW.getType());
        jsonObject.put("entityId", reviewId);
        notificationProducer.sendNotification("notifications", jsonObject.toString());
    }

    @Override
    public void dislikeSiteReview(int reviewId) {
        int userId = requestUtils.getUserIdCurrentRequest();

        siteReviewRepository.findById(reviewId)
                .orElseThrow(() -> new NotFoundException("Site review not found"));

        Optional<ReviewReactionEntity> reviewReactionEntityOpt = reviewReactionRepository.findByUserIdAndReviewId(userId, reviewId);

        ReviewReactionEntity reviewReactionEntity;
        if (reviewReactionEntityOpt.isPresent()) {
            reviewReactionEntity = reviewReactionEntityOpt.get();
            if (ReactionTypeEnum.DISLIKE.name().equals(reviewReactionEntity.getReactionType())) {
                reviewReactionEntity.setReactionType(null);
            } else {
                reviewReactionEntity.setReactionType(ReactionTypeEnum.DISLIKE.name());
            }
        } else {
            reviewReactionEntity = ReviewReactionEntity.builder()
                    .userId(userId)
                    .reviewId(reviewId)
                    .reactionType(ReactionTypeEnum.DISLIKE.name())
                    .build();
        }
        reviewReactionRepository.save(reviewReactionEntity);
    }

    @Override
    public PageDto<MySiteReviewRspnDto> getMySiteReviews(String reviewSearch, int page) {
        int userId = requestUtils.getUserIdCurrentRequest();

        Pageable pageable = PageRequest.of(page - 1, MY_SITE_REVIEW_LIMIT, Sort.by(Sort.Order.desc("createdAt")));

        Page<SiteReviewEntity> siteReviews = siteReviewRepository.findAllByUserIdAndCommentContainingIgnoreCase(userId, reviewSearch, pageable);

        PageDto<MySiteReviewRspnDto> mySiteReviews = pageMapper.toPageDto(siteReviews.map(siteReviewEntity -> siteReviewMapper.siteReviewEntityToMySiteReviewRspnDto(siteReviewEntity, userId)));

        mySiteReviews.getData().forEach(mySiteReviewRspnDto -> {
            SiteBasicInfoRspnDto siteBasicInfoRspnDto = siteService.getSiteBasicRepresentation(mySiteReviewRspnDto.getSiteId());
            mySiteReviewRspnDto.setSite(siteBasicInfoRspnDto);
        });

        return mySiteReviews;
    }
}
