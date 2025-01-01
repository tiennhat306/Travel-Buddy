package com.travelbuddy.admin;

import com.travelbuddy.common.paging.PageDto;
import com.travelbuddy.persistence.domain.entity.AdminEntity;

public interface AdminService {

    int getAdminIdByEmail(String username);
    PageDto<AdminEntity> getAdmins(int page, String search);
    AdminEntity getAdminById(int id);
    void associateAdminToGroup(int adminId, long groupId);
    void detachAdminFromGroup(int adminId, long groupId);
}
