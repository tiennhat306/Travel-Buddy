package com.travelbuddy.site.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.travelbuddy.common.exception.errorresponse.NotFoundException;
import com.travelbuddy.common.utils.RequestUtils;
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
import com.travelbuddy.user.UserService;
import com.travelbuddy.common.paging.PageDto;
import com.travelbuddy.persistence.domain.dto.sitereview.SiteReviewRspnDto;
import com.travelbuddy.sitereviews.SiteReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.net.URI;
import java.util.*;

@RestController
@RequestMapping("/api/sites")
@RequiredArgsConstructor
public class SiteController {
    private final SiteService siteService;
    private final SiteVersionService siteVersionService;
    private final SiteReviewService siteReviewService;
    private final RequestUtils requestUtils;

    @PostMapping
    public ResponseEntity<Object> postSite(@RequestBody @Valid SiteCreateRqstDto siteCreateRqstDto) {
        Integer siteID = siteService.createSiteWithSiteVersion(siteCreateRqstDto);
        return ResponseEntity.created(URI.create("/api/sites/" + siteID)).build();
    }

    @GetMapping("/{siteId}")
    public ResponseEntity<Object> getValidSiteRepresention(@PathVariable int siteId) {
        return ResponseEntity.ok(siteVersionService.getSiteView(siteId, requestUtils.getUserIdCurrentRequest()));
    }

    @GetMapping("/@")
    public ResponseEntity<Object> getSiteByLocation(@RequestParam double lat, @RequestParam double lng, @RequestParam double degRadius) {
        // Your logic to handle the request using lat and lon
        List<MapRepresentationDto> sitesInRange = siteVersionService.getSitesInRange(lat, lng, degRadius);
        return ResponseEntity.ok(sitesInRange);
    }

    @PutMapping
    public ResponseEntity<Object> updateSite(@RequestBody @Valid SiteUpdateRqstDto siteUpdateRqstDto) {
        siteService.updateSite(siteUpdateRqstDto, requestUtils.getUserIdCurrentRequest());
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<Object> getSiteVersion(@RequestParam(name = "version" , required = true) int siteVersionId) {
        SiteRepresentationDto siteRepresentationDto = siteVersionService.getSiteVersionView(siteVersionId, requestUtils.getUserIdCurrentRequest());
        return ResponseEntity.ok(siteRepresentationDto);
    }

    @GetMapping("/{siteId}/reviews")
    public ResponseEntity<Object> getSiteReviews(@PathVariable int siteId,
                                                 @RequestParam(name = "page", required = false, defaultValue = "1") int page) {
        PageDto<SiteReviewRspnDto> siteTypesPage = siteReviewService.getAllSiteReviews(siteId, page);
        return ResponseEntity.ok(siteTypesPage);
    }

    @PostMapping("/{siteId}/like")
    public ResponseEntity<Object> likeSite(@PathVariable int siteId) {
        siteService.likeSite(siteId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{siteId}/dislike")
    public ResponseEntity<Object> dislikeSite(@PathVariable int siteId) {
        siteService.dislikeSite(siteId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchSites(@RequestParam(name = "q", required = false, defaultValue = "") String siteSearch,
                                             @RequestParam(name = "page", required = false, defaultValue = "1") int page) {

        if (StringUtils.isBlank(siteSearch)) {
            return ResponseEntity.ok(List.of());
        }
        PageDto<SiteBasicInfoRspnDto> siteSearchRspnDto = siteService.searchSites(siteSearch, page);
        return ResponseEntity.ok(siteSearchRspnDto);
    }

    @GetMapping("/discover")
    public ResponseEntity<Object> discoverSites(@RequestParam(name = "page", required = false, defaultValue = "1") int page) {
        PageDto<SiteBasicInfoRspnDto> siteSearchRspnDto = siteService.discoverSites(page);
        return ResponseEntity.ok(siteSearchRspnDto);
    }

    @GetMapping("/my-sites")
    public ResponseEntity<Object> getMySites(@RequestParam(name = "page", required = false, defaultValue = "1") int page) {
        PageDto<SiteStatusRspndDto> siteSearchRspnDto = siteVersionService.getSiteStatuses(page, requestUtils.getUserIdCurrentRequest());
        return ResponseEntity.ok(siteSearchRspnDto);
    }
}
