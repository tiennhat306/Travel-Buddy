package com.travelbuddy.persistence.domain.dto.report.admin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SiteReportRspnDto {
    private Integer siteId;
    private String siteName;
    private String siteImageUrl;
    private Integer reportCount;

    public SiteReportRspnDto(Integer siteId, String siteName, String siteImageUrl, Long reportCount) {
        this.siteId = siteId;
        this.siteName = siteName;
        this.siteImageUrl = siteImageUrl;
        this.reportCount = reportCount.intValue();
    }
}
