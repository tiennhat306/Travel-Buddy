package com.travelbuddy.persistence.domain.dto.homepage;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HomepageOverviewRspnDto {
    private long totalUsers;
    private long totalAdmins;

    private long pendingUserReports;
    private long pendingSiteReports;
    private long pendingReviewReports;

    private long totalPendingApprovals;

    private long totalSites;
}
