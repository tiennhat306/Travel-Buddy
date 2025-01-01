package com.travelbuddy.persistence.domain.dto.account.admin;

import com.travelbuddy.persistence.domain.entity.PermissionEntity;

import java.util.List;

public class GroupBasicInfo {
    private Integer id;
    private String name;

    private List<PermissionEntity> permissions;
}
