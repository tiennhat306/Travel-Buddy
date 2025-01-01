package com.travelbuddy.admin;

import com.travelbuddy.common.paging.PageDto;
import com.travelbuddy.persistence.domain.entity.AdminEntity;

import java.util.List;

public interface AdminService {

    int getAdminIdByEmail(String username);
    PageDto<AdminEntity> getAdmins(int page, String search);
    AdminEntity getAdminById(int id);
    void associateAdminToGroup(int adminId, List<Integer> groupId);
    void detachAdminFromGroup(int adminId, List<Integer> groupId);
    void associatePermissionToGroup(long groupId, List<Integer> permissionId);
    void detachPermissionFromGroup(long groupId, List<Integer> permissionId);
}
