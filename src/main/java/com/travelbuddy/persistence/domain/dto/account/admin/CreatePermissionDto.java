package com.travelbuddy.persistence.domain.dto.account.admin;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreatePermissionDto {
    @NotEmpty(message = "Permission name is required")
    private String permissionName;
}
