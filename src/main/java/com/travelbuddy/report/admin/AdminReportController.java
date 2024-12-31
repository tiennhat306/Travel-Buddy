package com.travelbuddy.report.admin;

import com.travelbuddy.common.constants.ReportTypeEnum;
import com.travelbuddy.common.paging.PageDto;
import com.travelbuddy.persistence.domain.dto.report.ReportCategoryRspnDto;
import com.travelbuddy.persistence.domain.dto.report.admin.ReportDetailRspnDto;
import com.travelbuddy.persistence.domain.dto.report.admin.SiteReportRspnDto;
import com.travelbuddy.persistence.domain.dto.report.admin.SiteReviewReportRspnDto;
import com.travelbuddy.persistence.domain.dto.report.admin.UserReportRspnDto;
import com.travelbuddy.report.ReportTypeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("AdminReportController")
@RequestMapping("/api/admin/report")
@Slf4j
@RequiredArgsConstructor
public class AdminReportController {
    private final AdminReportService adminReportService;
    private final ReportTypeService reportTypeService;

    @GetMapping ("site/categories")
    public ResponseEntity<List<ReportCategoryRspnDto>> getCategories() {
        List<ReportCategoryRspnDto> categories = reportTypeService.getSiteReportCategories(ReportTypeEnum.SITE.getType());

        return ResponseEntity.ok(categories);
    }

    @GetMapping("site-review/categories")
    public ResponseEntity<List<ReportCategoryRspnDto>> getSiteReviewCategories() {
        List<ReportCategoryRspnDto> categories = reportTypeService.getSiteReportCategories(ReportTypeEnum.SITE_REVIEW.getType());

        return ResponseEntity.ok(categories);
    }

    @GetMapping("user/categories")
    public ResponseEntity<List<ReportCategoryRspnDto>> getUserCategories() {
        List<ReportCategoryRspnDto> categories = reportTypeService.getSiteReportCategories(ReportTypeEnum.USER.getType());

        return ResponseEntity.ok(categories);
    }

    @GetMapping("site")
    @PreAuthorize("hasAuthority('MANAGE_REPORTS')")
    public ResponseEntity<Object> getSiteReports(@RequestParam(name = "q", required = false, defaultValue = "") String search,
                                                                  @RequestParam(name = "page", required = false, defaultValue = "1") int page) {
        PageDto<SiteReportRspnDto> siteReports = adminReportService.getSiteReports(search, page);

        return ResponseEntity.ok(siteReports);
    }

    @GetMapping("site/banned")
    @PreAuthorize("hasAuthority('MANAGE_REPORTS')")
    public ResponseEntity<Object> getBannedSiteReports(@RequestParam(name = "q", required = false, defaultValue = "") String search,
                                                 @RequestParam(name = "page", required = false, defaultValue = "1") int page) {
        PageDto<SiteReportRspnDto> siteReports = adminReportService.getBannedSiteReports(search, page);

        return ResponseEntity.ok(siteReports);
    }

    @GetMapping("site/{siteId}")
    @PreAuthorize("hasAuthority('MANAGE_REPORTS')")
    public ResponseEntity<Object> getSiteReportsBySiteId(@PathVariable Integer siteId, @RequestParam(name = "page", required = false, defaultValue = "1") int page) {
        PageDto<ReportDetailRspnDto> siteReportDetails = adminReportService.getSiteReportsBySiteId(siteId, page);

        return ResponseEntity.ok(siteReportDetails);
    }

    @PutMapping("site/{siteId}/ban")
    @PreAuthorize("hasAuthority('MANAGE_REPORTS')")
    public ResponseEntity<Void> banSite(@PathVariable Integer siteId) {
        adminReportService.banSite(siteId);

        return ResponseEntity.noContent().build();
    }

    @PutMapping("site/{siteId}/unban")
    @PreAuthorize("hasAuthority('MANAGE_REPORTS')")
    public ResponseEntity<Void> unbanSite(@PathVariable Integer siteId) {
        adminReportService.unbanSite(siteId);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("site-review")
    @PreAuthorize("hasAuthority('MANAGE_REPORTS')")
    public ResponseEntity<Object> getSiteReviewReports(@RequestParam(name = "q", required = false, defaultValue = "") String search,
                                                       @RequestParam(name = "page", required = false, defaultValue = "1") int page) {
        PageDto<SiteReviewReportRspnDto> siteReviewReports = adminReportService.getSiteReviewReports(search, page);

        return ResponseEntity.ok(siteReviewReports);
    }

    @GetMapping("site-review/banned")
    @PreAuthorize("hasAuthority('MANAGE_REPORTS')")
    public ResponseEntity<Object> getBannedSiteReviewReports(@RequestParam(name = "q", required = false, defaultValue = "") String search,
                                                       @RequestParam(name = "page", required = false, defaultValue = "1") int page) {
        PageDto<SiteReviewReportRspnDto> siteReviewReports = adminReportService.getBannedSiteReviewReports(search, page);

        return ResponseEntity.ok(siteReviewReports);
    }

    @PutMapping("site-review/{siteReviewId}/ban")
    @PreAuthorize("hasAuthority('MANAGE_REPORTS')")
    public ResponseEntity<Void> banSiteReview(@PathVariable Integer siteReviewId) {
        adminReportService.banSiteReview(siteReviewId);

        return ResponseEntity.noContent().build();
    }

    @PutMapping("site-review/{siteReviewId}/unban")
    @PreAuthorize("hasAuthority('MANAGE_REPORTS')")
    public ResponseEntity<Void> unbanSiteReview(@PathVariable Integer siteReviewId) {
        adminReportService.unbanSiteReview(siteReviewId);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("site-review/{siteReviewId}")
    @PreAuthorize("hasAuthority('MANAGE_REPORTS')")
    public ResponseEntity<Object> getSiteReviewReportsBySiteReviewId(@PathVariable Integer siteReviewId, @RequestParam(name = "page", required = false, defaultValue = "1") int page) {
        PageDto<ReportDetailRspnDto> siteReviewReportDetails = adminReportService.getSiteReviewReportsBySiteReviewId(siteReviewId, page);

        return ResponseEntity.ok(siteReviewReportDetails);
    }

    @GetMapping("user")
    @PreAuthorize("hasAuthority('MANAGE_REPORTS')")
    public ResponseEntity<Object> getUserReports(@RequestParam(name = "q", required = false, defaultValue = "") String search,
                                                 @RequestParam(name = "page", required = false, defaultValue = "1") int page) {
        PageDto<UserReportRspnDto> userReports = adminReportService.getUserReports(search, page);

        return ResponseEntity.ok(userReports);
    }

    @GetMapping("user/banned")
    @PreAuthorize("hasAuthority('MANAGE_REPORTS')")
    public ResponseEntity<Object> getBannedUserReports(@RequestParam(name = "q", required = false, defaultValue = "") String search,
                                                 @RequestParam(name = "page", required = false, defaultValue = "1") int page) {
        PageDto<UserReportRspnDto> userReports = adminReportService.getBannedUserReports(search, page);

        return ResponseEntity.ok(userReports);
    }

    @PutMapping("user/{userId}/ban")
    @PreAuthorize("hasAuthority('MANAGE_REPORTS')")
    public ResponseEntity<Void> banUser(@PathVariable Integer userId) {
        adminReportService.banUser(userId);

        return ResponseEntity.noContent().build();
    }

    @PutMapping("user/{userId}/unban")
    @PreAuthorize("hasAuthority('MANAGE_REPORTS')")
    public ResponseEntity<Void> unbanUser(@PathVariable Integer userId) {
        adminReportService.unbanUser(userId);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("user/{userId}")
    @PreAuthorize("hasAuthority('MANAGE_REPORTS')")
    public ResponseEntity<Object> getUserReportsByUserId(@PathVariable Integer userId, @RequestParam(name = "page", required = false, defaultValue = "1") int page) {
        PageDto<ReportDetailRspnDto> userReportDetails = adminReportService.getUserReportsByUserId(userId, page);

        return ResponseEntity.ok(userReportDetails);
    }
}
