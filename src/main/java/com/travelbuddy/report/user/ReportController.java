package com.travelbuddy.report.user;

import com.travelbuddy.common.constants.ReportTypeEnum;
import com.travelbuddy.persistence.domain.dto.report.ReportCategoryRspnDto;
import com.travelbuddy.persistence.domain.dto.report.user.SiteReportRqstDto;
import com.travelbuddy.persistence.domain.dto.report.user.SiteReviewReportRqstDto;
import com.travelbuddy.persistence.domain.dto.report.user.UserReportRqstDto;
import com.travelbuddy.report.ReportTypeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/report")
@Slf4j
@RequiredArgsConstructor
public class ReportController {
    private final ReportService reportService;
    private final ReportTypeService reportTypeService;

    @GetMapping ("site/categories")
    public ResponseEntity<List<ReportCategoryRspnDto>> getCategories() {
        List<ReportCategoryRspnDto> categories = reportTypeService.getSiteReportCategories(ReportTypeEnum.SITE.getType());

        return ResponseEntity.ok(categories);
    }

    @PostMapping("site")
    public ResponseEntity<Void> reportSite(@RequestBody @Valid SiteReportRqstDto siteReportRqstDto) {
        reportService.reportSite(siteReportRqstDto);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("site-review/categories")
    public ResponseEntity<List<ReportCategoryRspnDto>> getSiteReviewCategories() {
        List<ReportCategoryRspnDto> categories = reportTypeService.getSiteReportCategories(ReportTypeEnum.SITE_REVIEW.getType());

        return ResponseEntity.ok(categories);
    }

    @PostMapping("site-review")
    public ResponseEntity<Void> reportSiteReview(@RequestBody @Valid SiteReviewReportRqstDto siteReviewReportRqstDto) {
        reportService.reportSiteReview(siteReviewReportRqstDto);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("user/categories")
    public ResponseEntity<List<ReportCategoryRspnDto>> getUserCategories() {
        List<ReportCategoryRspnDto> categories = reportTypeService.getSiteReportCategories(ReportTypeEnum.USER.getType());

        return ResponseEntity.ok(categories);
    }

    @PostMapping("user")
    public ResponseEntity<Void> reportUser(@RequestBody @Valid UserReportRqstDto userReportRqstDto) {
        reportService.reportUser(userReportRqstDto);

        return ResponseEntity.noContent().build();
    }

}
