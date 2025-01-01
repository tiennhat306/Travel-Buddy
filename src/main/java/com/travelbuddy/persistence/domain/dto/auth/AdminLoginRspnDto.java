package com.travelbuddy.persistence.domain.dto.auth;

import com.travelbuddy.persistence.domain.dto.account.admin.AdminDetailRspnDto;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class AdminLoginRspnDto {
    private String accessToken;

    private AdminDetailRspnDto adminInfo;
}
