package com.travelbuddy.persistence.domain.dto.report.admin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SiteReviewReportRspnDto {
    private Integer siteReviewId;
    private String siteReviewComment;
    private String siteReviewImageUrl;
    private Integer reportCount;

    public SiteReviewReportRspnDto(Integer siteReviewId, String siteReviewComment, String siteReviewImageUrl, Long reportCount) {
        this.siteReviewId = siteReviewId;
        this.siteReviewComment = siteReviewComment;
        this.siteReviewImageUrl = siteReviewImageUrl;
        this.reportCount = reportCount.intValue();
    }
}
