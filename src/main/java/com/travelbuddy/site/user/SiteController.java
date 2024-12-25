package com.travelbuddy.site.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.travelbuddy.common.exception.errorresponse.NotFoundException;
import com.travelbuddy.persistence.domain.dto.site.*;
import com.travelbuddy.persistence.domain.entity.BehaviorLogEntity;
import com.travelbuddy.persistence.domain.entity.FileEntity;
import com.travelbuddy.persistence.domain.entity.SiteEntity;
import com.travelbuddy.persistence.domain.entity.SiteMediaEntity;
import com.travelbuddy.persistence.repository.BehaviorLogRepository;
import com.travelbuddy.persistence.repository.SiteApprovalRepository;
import com.travelbuddy.persistence.repository.SiteRepository;
import com.travelbuddy.siteversion.user.SiteVersionService;
import com.travelbuddy.systemlog.admin.SystemLogService;
import com.travelbuddy.upload.cloud.StorageService;
import com.travelbuddy.user.UserService;
import com.travelbuddy.common.paging.PageDto;
import com.travelbuddy.persistence.domain.dto.sitereview.SiteReviewRspnDto;
import com.travelbuddy.sitereviews.SiteReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.net.URI;
import java.sql.Timestamp;
import java.util.*;

@RestController
@RequestMapping("/api/sites")
@RequiredArgsConstructor
public class SiteController {
    private final SiteService siteService;
    private final SiteApprovalRepository siteApprovalRepository;
    private final SiteVersionService siteVersionService;
    private final SiteRepository siteRepository;
    private final UserService userService;
    private final SiteReviewService siteReviewService;
    private final BehaviorLogRepository behaviorLogRepository;
    private final SystemLogService systemLogService;

    @PostMapping
    public ResponseEntity<Object> postSite(@RequestBody @Valid SiteCreateRqstDto siteCreateRqstDto) {
        Integer siteID = siteService.createSiteWithSiteVersion(siteCreateRqstDto);
        systemLogService.logInfo("Site with id " + siteID + " created");
        return ResponseEntity.created(URI.create("/api/sites/" + siteID)).build();
    }

    @GetMapping("/{siteId}")
    public ResponseEntity<Object> getValidSiteRepresention(@PathVariable int siteId) {
        /* This API returns the representation of siteID, publicly */
        Optional<Integer> latestApprovedVersionId = siteApprovalRepository.findLatestApprovedSiteVersionIdBySiteId(siteId);
        if (latestApprovedVersionId.isEmpty()) {
            throw new NotFoundException("No approved version found for site with id: " + siteId);
        }

        // Success block
        Integer siteVersionId = latestApprovedVersionId.get();
        SiteRepresentationDto representationDto = siteVersionService.getSiteVersionView(siteVersionId);
        BehaviorLogEntity behaviorLog = BehaviorLogEntity.builder()
                .timestamp(new Timestamp(System.currentTimeMillis()))
                .userId(userService.getUserIdByEmailOrUsername(SecurityContextHolder.getContext().getAuthentication().getName()))
                .siteId(siteId)
                .behavior("VIEW_SITE")
                .build();
        behaviorLogRepository.save(behaviorLog);
        return ResponseEntity.ok(representationDto);
    }

    @GetMapping("/@")
    public ResponseEntity<Object> getSiteByLocation(@RequestParam double lat, @RequestParam double lng, @RequestParam double degRadius) {
        // Your logic to handle the request using lat and lon
        List<MapRepresentationDto> sitesInRange = siteVersionService.getSitesInRange(lat, lng, degRadius);
        return ResponseEntity.ok(sitesInRange);
    }

    @PutMapping
    public ResponseEntity<Object> updateSite(@RequestBody @Valid SiteUpdateRqstDto siteUpdateRqstDto) {
        // 1. Check if the site exists, if not return 404
        Integer siteID = siteRepository.findById(siteUpdateRqstDto.getSiteId()).map(SiteEntity::getId).orElse(null);
        if (siteID == null) {
            return ResponseEntity.notFound().build();
        }

        // 2. Chek if the cookie username/id is the ownerId
        Integer ownerId = siteRepository.findById(siteUpdateRqstDto.getSiteId()).map(SiteEntity::getOwnerId).orElse(null);
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Integer userId = userService.getUserIdByEmailOrUsername(username);
        if (!userId.equals(ownerId)) {
            return ResponseEntity.status(403).build();
        }

        // 3. Update the site
        siteService.updateSite(siteUpdateRqstDto);
        systemLogService.logInfo("Site with id " + siteUpdateRqstDto.getSiteId() + " updated");
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<Object> getSiteVersion(@RequestParam(name = "version" , required = true) int siteVersionId) {
        // 1. Get the site representation
        SiteRepresentationDto siteRepresentationDto = siteVersionService.getSiteVersionView(siteVersionId);

        // 2. Check for the owner permission
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Integer userId = userService.getUserIdByEmailOrUsername(username);
        Integer ownerId = siteRepresentationDto.getOwnerId();
        if (!userId.equals(ownerId)) {
            return ResponseEntity.status(403).build();
        }
        return ResponseEntity.ok(siteRepresentationDto);
    }

    @GetMapping("/{siteId}/reviews")
    public ResponseEntity<Object> getSiteReviews(@PathVariable int siteId,
                                                 @RequestParam(name = "page", required = false, defaultValue = "1") int page) {
        PageDto<SiteReviewRspnDto> siteTypesPage = siteReviewService.getAllSiteReviews(siteId, page);
        BehaviorLogEntity behaviorLog = BehaviorLogEntity.builder()
                .timestamp(new Timestamp(System.currentTimeMillis()))
                .userId(userService.getUserIdByEmailOrUsername(SecurityContextHolder.getContext().getAuthentication().getName()))
                .siteId(siteId)
                .behavior("READ_REVIEWS")
                .build();
        behaviorLogRepository.save(behaviorLog);
        return ResponseEntity.ok(siteTypesPage);
    }

    @PostMapping("/{siteId}/like")
    public ResponseEntity<Object> likeSite(@PathVariable int siteId) {
        siteService.likeSite(siteId);
        BehaviorLogEntity behaviorLog = BehaviorLogEntity.builder()
                .timestamp(new Timestamp(System.currentTimeMillis()))
                .userId(userService.getUserIdByEmailOrUsername(SecurityContextHolder.getContext().getAuthentication().getName()))
                .siteId(siteId)
                .behavior("LIKE_SITE")
                .build();
        behaviorLogRepository.save(behaviorLog);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{siteId}/dislike")
    public ResponseEntity<Object> dislikeSite(@PathVariable int siteId) {
        siteService.dislikeSite(siteId);
        BehaviorLogEntity behaviorLog = BehaviorLogEntity.builder()
                .timestamp(new Timestamp(System.currentTimeMillis()))
                .userId(userService.getUserIdByEmailOrUsername(SecurityContextHolder.getContext().getAuthentication().getName()))
                .siteId(siteId)
                .behavior("DISLIKE_SITE")
                .build();
        behaviorLogRepository.save(behaviorLog);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchSites(@RequestParam(name = "q", required = false, defaultValue = "") String siteSearch,
                                             @RequestParam(name = "page", required = false, defaultValue = "1") int page) {

        if (StringUtils.isBlank(siteSearch)) {
            return ResponseEntity.ok(List.of());
        }

        PageDto<SiteBasicInfoRspnDto> siteSearchRspnDto = siteService.searchSites(siteSearch, page);
        BehaviorLogEntity behaviorLog = BehaviorLogEntity.builder()
                .timestamp(new Timestamp(System.currentTimeMillis()))
                .userId(userService.getUserIdByEmailOrUsername(SecurityContextHolder.getContext().getAuthentication().getName()))
                .siteId(null)
                .behavior("SEARCH_SITE")
                .extraInfo(siteSearch)
                .build();
        behaviorLogRepository.save(behaviorLog);
        return ResponseEntity.ok(siteSearchRspnDto);
    }

    @GetMapping("/discover")
    public ResponseEntity<Object> discoverSites(@RequestParam(name = "page", required = false, defaultValue = "1") int page) {
        PageDto<SiteBasicInfoRspnDto> siteSearchRspnDto = siteService.discoverSites(page);
        return ResponseEntity.ok(siteSearchRspnDto);
    }

    @GetMapping("/my-sites")
    public ResponseEntity<Object> getMySites(@RequestParam(name = "page", required = false, defaultValue = "1") int page) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Integer userId = userService.getUserIdByEmailOrUsername(username);

        PageDto<SiteStatusRspndDto> siteSearchRspnDto = siteVersionService.getSiteStatuses(page, userId);

        return ResponseEntity.ok(siteSearchRspnDto);
    }
}
