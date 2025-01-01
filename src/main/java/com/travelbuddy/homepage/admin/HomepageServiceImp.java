package com.travelbuddy.homepage.admin;

import com.travelbuddy.persistence.domain.dto.homepage.HomepageOverviewRspnDto;
import com.travelbuddy.persistence.repository.AdminRepository;
import com.travelbuddy.persistence.repository.SiteApprovalRepository;
import com.travelbuddy.persistence.repository.SiteRepository;
import com.travelbuddy.persistence.repository.UserRepository;
import com.travelbuddy.report.admin.AdminReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HomepageServiceImp implements HomepageService{
    private final UserRepository userRepository;
    private final AdminRepository adminRepository;
    private final AdminReportService adminReportService;
    private final SiteApprovalRepository siteApprovalRepository;
    private final SiteRepository siteRepository;

    @Override
    public HomepageOverviewRspnDto getHomepageOverview() {
        HomepageOverviewRspnDto homepageOverviewRspnDto = new HomepageOverviewRspnDto();

        homepageOverviewRspnDto.setTotalUsers(userRepository.count());
        homepageOverviewRspnDto.setTotalAdmins(adminRepository.count());
        homepageOverviewRspnDto.setPendingUserReports(adminReportService.getUserReports("", 1).getPagination().getTotalItems());
        homepageOverviewRspnDto.setPendingSiteReports(adminReportService.getSiteReports("", 1).getPagination().getTotalItems());
        homepageOverviewRspnDto.setPendingReviewReports(adminReportService.getSiteReviewReports("", 1).getPagination().getTotalItems());
        homepageOverviewRspnDto.setTotalPendingApprovals(siteApprovalRepository.countAllByStatus(null));
        homepageOverviewRspnDto.setTotalSites(siteRepository.count());

        return homepageOverviewRspnDto;
    }
}
