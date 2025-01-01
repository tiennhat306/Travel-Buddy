package com.travelbuddy.persistence.domain.dto.account.admin;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminUpdateRqstDto {
    private Integer id;
    private String address;
    private String phoneNumber;
    private String avatarId;
    private String avatarUrl;
}
