package com.travelbuddy.persistence.domain.dto.report.admin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReportDetailRspnDto {
    private Integer reportId;
    private Integer userId;
    private String userName;
    private String userImageUrl;
    private Integer categoryId;
    private String categoryName;
    private String description;
    private String createdAt;
}
