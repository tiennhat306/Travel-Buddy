package com.travelbuddy.persistence.domain.dto.report.user;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SiteReportRqstDto {
    @NotNull
    private Integer siteId;
    @NotNull
    private Integer categoryId;
    private String description;
}
