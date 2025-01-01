package com.travelbuddy.persistence.domain.dto.account.admin;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateGroupRqstDto {
    @NotEmpty(message = "Group name is required")
    private String name;
}
