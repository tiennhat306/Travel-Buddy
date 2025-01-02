package com.travelbuddy.admin;

import com.travelbuddy.common.paging.PageDto;
import com.travelbuddy.persistence.domain.dto.account.admin.*;
import com.travelbuddy.persistence.domain.entity.AdminEntity;

import java.util.List;

public interface AdminService {

    int getAdminIdByEmail(String username);
    PageDto<AdminRspdDto> getAdmins(int page, String search);
    AdminRspdDto getAdminById(int id);
    void associateAdminToGroup(int adminId, List<Integer> groupId);
    void detachAdminFromGroup(int adminId, List<Integer> groupId);
    void associatePermissionToGroup(long groupId, List<Integer> permissionId);
    void detachPermissionFromGroup(long groupId, List<Integer> permissionId);
    Integer createAdmin(CreateAdminRqstDto createAdminRqstDto);
    void updateAdmin(AdminUpdateRqstDto adminUpdateRqstDto);
    void disableAdmin(int id);
    void enableAdmin(int id);
    void resetPassword(AdminResetPasswordRqstDto adminResetPasswordRqstDto);
    void newAdminGroup(String group);
    void newAdminPermission(String permission);
}
