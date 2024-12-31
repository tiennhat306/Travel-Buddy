package com.travelbuddy.persistence.domain.dto.report.user;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UserReportRqstDto {
    @NotNull
    private Integer userId;
    @NotNull
    private Integer categoryId;
    private String description;
}
