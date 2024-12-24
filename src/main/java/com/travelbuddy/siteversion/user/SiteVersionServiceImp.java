package com.travelbuddy.siteversion.user;

import com.travelbuddy.common.constants.ReactionTypeEnum;
import com.travelbuddy.common.exception.errorresponse.NotFoundException;
import com.travelbuddy.common.mapper.PageMapper;
import com.travelbuddy.common.paging.PageDto;
import com.travelbuddy.common.utils.RequestUtils;
import com.travelbuddy.fee.FeeService;
import com.travelbuddy.persistence.domain.dto.site.*;
import com.travelbuddy.persistence.domain.dto.sitereview.MediaRspnDto;
import com.travelbuddy.persistence.domain.dto.siteservice.GroupedSiteServicesRspnDto;
import com.travelbuddy.persistence.domain.dto.sitetype.SiteTypeRspnDto;
import com.travelbuddy.persistence.domain.entity.*;
import com.travelbuddy.persistence.repository.*;
import com.travelbuddy.service.admin.ServiceService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.travelbuddy.common.constants.GeographicLimitConstants.MAX_DEG_RADIUS;
import static com.travelbuddy.common.constants.GeographicLimitConstants.MIN_DEG_RADIUS;
import static com.travelbuddy.common.constants.PaginationLimitConstants.MY_SITE_LIMIT;

@Service
@Transactional
@RequiredArgsConstructor
public class SiteVersionServiceImp implements SiteVersionService {
    private final SiteVersionRepository siteVersionRepository;
    private final SiteRepository siteRepository;
    private final UserRepository userRepository;
    private final ServiceService serviceService;
    private final PhoneNumberRepository phoneNumberRepository;
    private final OpeningTimeRepository openingTimeRepository;
    private final SiteTypeRepository siteTypeRepository;
    private final SiteReactionRepository siteReactionRepository;
    private final RequestUtils requestUtils;
    private final SiteMediaRepository siteMediaRepository;
    private final SiteReviewRepository siteReviewRepository;
    private final FeeService feeService;
    private final SiteApprovalRepository siteApprovalRepository;
    private final PageMapper pageMapper;

    @Override
    @Transactional
    public int createSiteVersion(SiteCreateRqstDto siteCreateRqstDto, int siteId) {
        SiteVersionEntity siteVersion = SiteVersionEntity.builder()
                .siteName(siteCreateRqstDto.getSiteName())
                .lat(siteCreateRqstDto.getLat())
                .lng(siteCreateRqstDto.getLng())
                .resolvedAddress(siteCreateRqstDto.getResolvedAddress())
                .website(siteCreateRqstDto.getWebsite())
                .createdAt(LocalDateTime.now())
                .typeId(siteCreateRqstDto.getTypeId())
                .siteId(siteId)
                .build();

        return siteVersionRepository.save(siteVersion).getId();
    }

    @Override
    @Transactional
    public SiteRepresentationDto getSiteVersionView(Integer siteVersionId) {
        SiteRepresentationDto resndBody = new SiteRepresentationDto();
        // 1. Get basic infor - siteVersion
        SiteVersionEntity siteVersionInfos = siteVersionRepository.findById(siteVersionId)
                .orElseThrow(() -> new NotFoundException("Site version not found"));

        // 1'. Get phone numbers
        List<String> phoneNumbers = phoneNumberRepository.findAllBySiteVersionId(siteVersionId)
                .orElseThrow(() -> new NotFoundException("Phone numbers not found"))
                .stream()
                .map(PhoneNumberEntity::getPhoneNumber)
                .toList();        // 1''. Get opening times
        List<OpeningTimeEntity> openingTimes = openingTimeRepository.findAllBySiteVersionId(siteVersionId).orElseThrow(() -> new NotFoundException("Opening times not found"));
        // 2. Get user basic information
        Integer userId = siteRepository.findById(siteVersionInfos.getSiteId())
                .orElseThrow(() -> new NotFoundException("Site not found"))
                .getOwnerId();

        UserEntity userInfo = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        // 3. Get support services
        List<GroupedSiteServicesRspnDto> groupedServices = serviceService.getGroupedSiteServices(siteVersionId);
        resndBody.mapView(siteVersionInfos, userInfo, groupedServices, phoneNumbers, openingTimes);

        // 4. Get reactions
        resndBody.setUserReaction(getReactionType(siteVersionInfos.getSiteId()));
        resndBody.setLikeCount(siteReactionRepository.countBySiteIdAndReactionType(siteVersionInfos.getSiteId(), ReactionTypeEnum.LIKE.name()));
        resndBody.setDislikeCount(siteReactionRepository.countBySiteIdAndReactionType(siteVersionInfos.getSiteId(), ReactionTypeEnum.DISLIKE.name()));

        // 5. Get medias
        List<SiteMediaEntity> siteMediaEntities = siteMediaRepository.findAllBySiteId(siteVersionInfos.getSiteId());
        resndBody.setMedias(siteMediaEntities.stream()
                .map(siteMediaEntity -> MediaRspnDto.builder()
                        .id(siteMediaEntity.getId())
                        .url(siteMediaEntity.getMedia().getUrl())
                        .mediaType(siteMediaEntity.getMediaType())
                        .createdAt(String.valueOf(siteMediaEntity.getMedia().getCreatedAt()))
                        .build())
                .toList());

        // 6. Get rating
        resndBody.setAverageRating(siteReviewRepository.getAverageGeneralRatingBySiteId(siteVersionInfos.getSiteId()));
        resndBody.setTotalRating(siteReviewRepository.countBySiteId(siteVersionInfos.getSiteId()));
        resndBody.setFiveStarRating(siteReviewRepository.countBySiteIdAndGeneralRating(siteVersionInfos.getSiteId(), 5));
        resndBody.setFourStarRating(siteReviewRepository.countBySiteIdAndGeneralRating(siteVersionInfos.getSiteId(), 4));
        resndBody.setThreeStarRating(siteReviewRepository.countBySiteIdAndGeneralRating(siteVersionInfos.getSiteId(), 3));
        resndBody.setTwoStarRating(siteReviewRepository.countBySiteIdAndGeneralRating(siteVersionInfos.getSiteId(), 2));
        resndBody.setOneStarRating(siteReviewRepository.countBySiteIdAndGeneralRating(siteVersionInfos.getSiteId(), 1));

        // 7. Get fees
        resndBody.setFees(feeService.getFeeBySiteVersionId(siteVersionId));
        return resndBody;
    }

    @Override
    public List<MapRepresentationDto> getSitesInRange(double lat, double lng, double degRadius) {
        /*
            This function is to return Map representation of sites in the given range.
        */
        if (lat < -90 || lat > 90 || lng < -180 || lng > 180 || degRadius < MIN_DEG_RADIUS || degRadius > MAX_DEG_RADIUS) {
            throw new IllegalArgumentException("Invalid latitude or longitude");
        }
        List<MapRepresentationDto> sitesInRange = new ArrayList<>();
        // Get latest siteVersions, and approved, group by siteId
        List<SiteVersionEntity> siteVersions = siteVersionRepository.findApprovedSiteVersionsInRange(lat, lng, degRadius);
        for (SiteVersionEntity siteVersion : siteVersions) {
            SiteTypeEntity siteType = siteTypeRepository.findById(siteVersion.getTypeId())
                    .orElseThrow(() -> new NotFoundException("Site type not found"));

            List<MediaRspnDto> medias = getSiteMedias(siteVersion.getSiteId());

            Double averageRating = siteReviewRepository.getAverageGeneralRatingBySiteId(siteVersion.getSiteId());
            Integer totalRating = siteReviewRepository.countBySiteId(siteVersion.getSiteId());

            MapRepresentationDto mapRepresentationDto = MapRepresentationDto.builder()
                    .siteId(siteVersion.getSiteId())
                    .siteType(new SiteTypeRspnDto(siteType))
                    .name(siteVersion.getSiteName())
                    .lat(siteVersion.getLat())
                    .lng(siteVersion.getLng())
                    .medias(medias)
                    .averageRating(averageRating)
                    .totalRating(totalRating)
                    .build();
            sitesInRange.add(mapRepresentationDto);
        }
        return sitesInRange;
    }

    @Override
    @Transactional
    public Integer updateSiteVersion(SiteUpdateRqstDto siteUpdateRqstDto) {
        /* This method works like a CREATE method
           returns: ID of the new site version
        * */
        SiteVersionEntity siteVersion = SiteVersionEntity.builder()
                .siteName(siteUpdateRqstDto.getNewSiteName())
                .lat(siteUpdateRqstDto.getNewLat())
                .lng(siteUpdateRqstDto.getNewLng())
                .resolvedAddress(siteUpdateRqstDto.getNewResolvedAddress())
                .website(siteUpdateRqstDto.getNewWebSite())
                .createdAt(LocalDateTime.now())
                .typeId(siteUpdateRqstDto.getNewTypeId())
                .siteId(siteUpdateRqstDto.getSiteId())
                .description(siteUpdateRqstDto.getNewDescription())
                .build();
        return siteVersionRepository.save(siteVersion).getId();
    }

    @Override
    public SiteBasicInfoRspnDto getSiteVersionBasicView(Integer siteVersionId) {
        SiteBasicInfoRspnDto resndBody = new SiteBasicInfoRspnDto();
        // 1. Get basic infor - siteVersion
        SiteVersionEntity siteVersionInfos = siteVersionRepository.findById(siteVersionId)
                .orElseThrow(() -> new NotFoundException("Site version not found"));

        // 2. Get user basic information
        Integer userId = siteRepository.findById(siteVersionInfos.getSiteId())
                .orElseThrow(() -> new NotFoundException("Site not found"))
                .getOwnerId();

        UserEntity userInfo = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        // 3. Get support services
        resndBody.mapView(siteVersionInfos, userInfo);

        // 4. Get reactions
        resndBody.setUserReaction(getReactionType(siteVersionInfos.getSiteId()));
        resndBody.setLikeCount(siteReactionRepository.countBySiteIdAndReactionType(siteVersionInfos.getSiteId(), ReactionTypeEnum.LIKE.name()));
        resndBody.setDislikeCount(siteReactionRepository.countBySiteIdAndReactionType(siteVersionInfos.getSiteId(), ReactionTypeEnum.DISLIKE.name()));

        // 5. Get rating
        resndBody.setAverageRating(siteReviewRepository.getAverageGeneralRatingBySiteId(siteVersionInfos.getSiteId()));
        resndBody.setTotalRating(siteReviewRepository.countBySiteId(siteVersionInfos.getSiteId()));

        // 6. Get medias
        resndBody.setMedias(getSiteMedias(siteVersionInfos.getSiteId()));

        return resndBody;
    }

    private List<MediaRspnDto> getSiteMedias(int siteId) {
        List<SiteMediaEntity> siteMediaEntities = siteMediaRepository.findAllBySiteId(siteId);

        return siteMediaEntities.stream()
                .map(siteMediaEntity -> MediaRspnDto.builder()
                        .id(siteMediaEntity.getId())
                        .url(siteMediaEntity.getMedia().getUrl())
                        .mediaType(siteMediaEntity.getMediaType())
                        .createdAt(String.valueOf(siteMediaEntity.getMedia().getCreatedAt()))
                        .build())
                .toList();
    }

    private String getReactionType(int siteId) {
        int userId;
        try {
            userId = requestUtils.getUserIdCurrentRequest();
        } catch (Exception e) {
            return null;
        }
        SiteReactionEntity siteReactionEntity = siteReactionRepository.findByUserIdAndSiteId(userId, siteId).orElse(null);
        if (siteReactionEntity == null) {
            return null;
        }
        return siteReactionEntity.getReactionType();
    }

    @Override
    public PageDto<SiteStatusRspndDto> getSiteStatuses(int page, int userId) {
        Pageable pageable = PageRequest.of(page - 1, MY_SITE_LIMIT, Sort.by("createdAt"));
        /*List<SiteVersionEntity> siteVersions = siteVersionRepository.findAllByOwnerId(userId);
        List<SiteStatusRspndDto> siteStatuses = new ArrayList<>();
        for (SiteVersionEntity siteVersion : siteVersions) {
            SiteApprovalEntity siteApproval = siteApprovalRepository.findBySiteVersionId(siteVersion.getId());
            SiteMediaEntity siteMedia = siteMediaRepository.findFirstBySiteIdAndMediaType(siteVersion.getSiteId(), "IMAGE");
            siteStatuses.add(new SiteStatusRspndDto(siteVersion, siteApproval, siteMedia));
        }*/
        Page<SiteVersionEntity> siteVersions = siteVersionRepository.findAllByOwnerId(userId, pageable);
        List<SiteStatusRspndDto> siteStatuses = siteVersions.stream()
                .map(siteVersion -> {
                    SiteApprovalEntity siteApproval = siteApprovalRepository.findBySiteVersionId(siteVersion.getId());
                    SiteMediaEntity siteMedia = siteMediaRepository.findFirstBySiteIdAndMediaType(siteVersion.getSiteId(), "IMAGE");
                    return new SiteStatusRspndDto(siteVersion, siteApproval, siteMedia);
                })
                .toList();
        Page<SiteStatusRspndDto> siteStatusesPage = new PageImpl<>(siteStatuses, pageable, siteVersions.getTotalElements());
        return pageMapper.toPageDto(siteStatusesPage);
    }
}
