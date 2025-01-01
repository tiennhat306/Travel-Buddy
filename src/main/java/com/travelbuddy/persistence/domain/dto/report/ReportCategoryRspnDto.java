package com.travelbuddy.persistence.domain.dto.report;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReportCategoryRspnDto {
    private Integer id;
    private String name;
    private String description;
}
