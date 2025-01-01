package com.travelbuddy.persistence.domain.dto.homepage;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HomepageOverviewRspnDto {
    private int totalUsers;
    private int totalAdmins;

    private int pendingUserReports;
    private int pendingSiteReports;
    private int pendingReviewReports;

    private int totalPendingApprovals;

    private int totalSites;
}
