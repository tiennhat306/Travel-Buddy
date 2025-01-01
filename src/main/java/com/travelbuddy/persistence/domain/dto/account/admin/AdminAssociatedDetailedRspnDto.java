package com.travelbuddy.persistence.domain.dto.account.admin;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
@Builder
public class AdminAssociatedDetailedRspnDto {
    private AdminDetailRspnDto basicInfo;
}
