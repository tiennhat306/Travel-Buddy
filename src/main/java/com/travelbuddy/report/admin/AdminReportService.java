package com.travelbuddy.report.admin;

import com.travelbuddy.common.paging.PageDto;
import com.travelbuddy.persistence.domain.dto.report.admin.ReportDetailRspnDto;
import com.travelbuddy.persistence.domain.dto.report.admin.SiteReportRspnDto;
import com.travelbuddy.persistence.domain.dto.report.admin.SiteReviewReportRspnDto;
import com.travelbuddy.persistence.domain.dto.report.admin.UserReportRspnDto;

public interface AdminReportService {
    PageDto<SiteReportRspnDto> getSiteReports(String reviewSearch, int page);

    PageDto<SiteReportRspnDto> getBannedSiteReports(String search, int page);

    PageDto<ReportDetailRspnDto> getSiteReportsBySiteId(Integer siteId, int page);

    void banSite(Integer siteId);

    void unbanSite(Integer siteId);

    PageDto<SiteReviewReportRspnDto> getSiteReviewReports(String search, int page);

    PageDto<SiteReviewReportRspnDto> getBannedSiteReviewReports(String search, int page);

    void banSiteReview(Integer siteReviewId);

    void unbanSiteReview(Integer siteReviewId);

    PageDto<UserReportRspnDto> getUserReports(String search, int page);

    PageDto<UserReportRspnDto> getBannedUserReports(String search, int page);

    void banUser(Integer userId);

    void unbanUser(Integer userId);

    PageDto<ReportDetailRspnDto> getSiteReviewReportsBySiteReviewId(Integer siteReviewId, int page);

    PageDto<ReportDetailRspnDto> getUserReportsByUserId(Integer userId, int page);
}
