package com.travelbuddy.permission.admin;

import com.travelbuddy.common.paging.PageDto;
import com.travelbuddy.persistence.domain.entity.PermissionEntity;
import java.util.List;

public interface PermissionService {
    PageDto<PermissionEntity> getPermissions(int page, String search);
    List<PermissionEntity> getAllPermissions();
    PermissionEntity getPermissionById(long id);
}
