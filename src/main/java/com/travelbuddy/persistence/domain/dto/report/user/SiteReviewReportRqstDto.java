package com.travelbuddy.persistence.domain.dto.report.user;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SiteReviewReportRqstDto {
    @NotNull
    private Integer siteReviewId;
    @NotNull
    private Integer categoryId;
    private String description;
}
