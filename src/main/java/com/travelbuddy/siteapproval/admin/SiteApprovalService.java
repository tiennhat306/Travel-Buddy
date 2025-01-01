package com.travelbuddy.siteapproval.admin;

import com.travelbuddy.common.paging.PageDto;
import com.travelbuddy.persistence.domain.dto.siteapproval.GeneralViewSiteApprovalRspndDto;
import com.travelbuddy.persistence.domain.dto.siteapproval.UpdateSiteApprovalRqstDto;

public interface SiteApprovalService {
    void createDefaultSiteApproval(Integer siteVersionId);
    void updateSiteApproval(UpdateSiteApprovalRqstDto updateSiteApprovalRqstDto, Integer adminId);
    PageDto<GeneralViewSiteApprovalRspndDto> getPendingSiteApprovals(int page);
    Integer getLatestApprovedSiteVersionIdBySiteId(Integer siteId);
}
