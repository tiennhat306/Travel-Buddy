package com.travelbuddy.persistence.domain.dto.site;

import com.travelbuddy.persistence.domain.dto.fee.FeeRspndDto;
import com.travelbuddy.persistence.domain.dto.sitereview.MediaRspnDto;
import com.travelbuddy.persistence.domain.dto.sitetype.SiteTypeRspnDto;
import com.travelbuddy.persistence.domain.entity.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SiteBasicInfoRspnDto {
    private Integer siteId;
    private Integer siteVersionId;

    private Integer likeCount;
    private Integer dislikeCount;
    private String userReaction;

    // Owner informations
    private Integer ownerId;
    private String ownerUsername;
    private String ownerProfilePicture;

    // SiteVersion informations - main informations showing the user to view
    private String siteName;
    private double lat;
    private double lng;
    private String resolvedAddress;
    private String website;
    private String createdAt;
    private SiteTypeRspnDto siteType;
    private String description;

    private List<MediaRspnDto> medias;

    private Double averageRating;
    private Integer totalRating;

    public void mapSiteVersion(SiteVersionEntity siteVersion) {
        this.siteId = siteVersion.getSiteId();
        this.siteVersionId = siteVersion.getId();
        this.siteName = siteVersion.getSiteName();
        this.lat = siteVersion.getLat();
        this.lng = siteVersion.getLng();
        this.resolvedAddress = siteVersion.getResolvedAddress();
        this.website = siteVersion.getWebsite();
        this.createdAt = siteVersion.getCreatedAt().toString();
        this.description = siteVersion.getDescription();

        // Mapping the siteType
        this.siteType = new SiteTypeRspnDto(siteVersion.getSiteType());
    }

    public void mapUser(UserEntity userEntity) {
        this.ownerId = userEntity.getId();
        this.ownerUsername = userEntity.getNickname();
        this.ownerProfilePicture = null;
    }

    public void mapView(SiteVersionEntity siteVersion, UserEntity userEntity) {
        mapSiteVersion(siteVersion);
        mapUser(userEntity);
    }

    public List<MediaRspnDto> getSiteMedias(SiteEntity siteEntity) {
        List<SiteMediaEntity> siteMediaEntities = siteEntity.getSiteMedias();

        return siteMediaEntities.stream()
                .map(siteMediaEntity -> MediaRspnDto.builder()
                        .id(siteMediaEntity.getId())
                        .url(siteMediaEntity.getMedia().getUrl())
                        .mediaType(siteMediaEntity.getMediaType())
                        .createdAt(String.valueOf(siteMediaEntity.getMedia().getCreatedAt()))
                        .build())
                .toList();
    }

    public SiteBasicInfoRspnDto(SiteVersionEntity siteVersionEntity) {
        this.siteId = siteVersionEntity.getSiteId();
        this.siteVersionId = siteVersionEntity.getId();
        this.siteName = siteVersionEntity.getSiteName();
        this.lat = siteVersionEntity.getLat();
        this.lng = siteVersionEntity.getLng();
        this.resolvedAddress = siteVersionEntity.getResolvedAddress();
        this.website = siteVersionEntity.getWebsite();
        this.createdAt = siteVersionEntity.getCreatedAt().toString();
        this.siteType = new SiteTypeRspnDto(siteVersionEntity.getSiteType());
        this.description = siteVersionEntity.getDescription();

        // Mapping the owner
        UserEntity owner = siteVersionEntity.getSiteEntity().getUserEntity();
        this.ownerId = owner.getId();
        this.ownerUsername = owner.getNickname();
        this.ownerProfilePicture = owner.getAvatar() != null ? owner.getAvatar().getUrl() : null;

        // Mapping the reactions
        this.likeCount = (int) siteVersionEntity.getSiteEntity().getSiteReactions().stream()
                .filter(siteReactionEntity -> "LIKE".equals(siteReactionEntity.getReactionType()))
                .count();
        this.dislikeCount = (int) siteVersionEntity.getSiteEntity().getSiteReactions().stream()
                .filter(siteReactionEntity -> "DISLIKE".equals(siteReactionEntity.getReactionType()))
                .count();
        this.userReaction = siteVersionEntity.getSiteEntity().getSiteReactions().stream()
                .filter(siteReactionEntity -> owner.getId().equals(siteReactionEntity.getUserId()))
                .findFirst()
                .map(SiteReactionEntity::getReactionType)
                .orElse(null);

        this.medias = getSiteMedias(siteVersionEntity.getSiteEntity());

        // Mapping the ratings
        this.totalRating = siteVersionEntity.getSiteEntity().getSiteReviewEntities().size();
        this.averageRating = siteVersionEntity.getSiteEntity().getSiteReviewEntities().stream()
                .mapToDouble(SiteReviewEntity::getGeneralRating)
                .average()
                .orElse(0);
    }
}
