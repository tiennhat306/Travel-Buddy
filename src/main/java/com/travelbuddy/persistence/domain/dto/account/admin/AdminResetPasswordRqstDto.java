package com.travelbuddy.persistence.domain.dto.account.admin;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminResetPasswordRqstDto {
    private Integer adminId;
    private String password;
}
