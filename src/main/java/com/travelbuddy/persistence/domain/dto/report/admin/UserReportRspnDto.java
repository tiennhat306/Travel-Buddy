package com.travelbuddy.persistence.domain.dto.report.admin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserReportRspnDto {
    private Integer userId;
    private String userName;
    private String userImageUrl;
    private Integer reportCount;

    public UserReportRspnDto(Integer userId, String userName, String userImageUrl, Long reportCount) {
        this.userId = userId;
        this.userName = userName;
        this.userImageUrl = userImageUrl;
        this.reportCount = reportCount.intValue();
    }
}
