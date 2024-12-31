package com.travelbuddy.report.user;

import com.travelbuddy.persistence.domain.dto.report.user.SiteReportRqstDto;
import com.travelbuddy.persistence.domain.dto.report.user.SiteReviewReportRqstDto;
import com.travelbuddy.persistence.domain.dto.report.user.UserReportRqstDto;

public interface ReportService {
    void reportSite(SiteReportRqstDto siteReportRqstDto);

    void reportSiteReview(SiteReviewReportRqstDto siteReviewReportRqstDto);

    void reportUser(UserReportRqstDto userReportRqstDto);
}
