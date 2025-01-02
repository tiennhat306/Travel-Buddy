package com.travelbuddy.auth.controller;

import com.travelbuddy.admin.AdminService;
import com.travelbuddy.common.paging.PageDto;
import com.travelbuddy.groups.admin.GroupService;
import com.travelbuddy.permission.admin.PermissionService;
import com.travelbuddy.persistence.domain.dto.account.admin.*;
import com.travelbuddy.persistence.domain.dto.auth.ResetPasswordRqstDto;
import com.travelbuddy.persistence.domain.entity.AdminEntity;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
public class AdminController {
    private final AdminService adminService;
    private final GroupService groupService;
    private final PermissionService permissionService;

    @PreAuthorize("hasAuthority('MANAGE_USERS')")
    @PostMapping("/manage-user")
    public ResponseEntity<String> manageUser() {
        return ResponseEntity.ok("Manage user");
    }

    @PreAuthorize("hasAuthority('MANAGE_GROUPS')")
    @PostMapping("/manage-group")
    public ResponseEntity<String> manageGroup() {
        return ResponseEntity.ok("Manage group");
    }

    @GetMapping("/admin-accounts")
    @PreAuthorize("hasAuthority('MANAGE_ADMINS')") // TODO: Make this to two
    public ResponseEntity<Object> getAccounts(@RequestParam(name = "page", defaultValue = "1") int page,
                                              @RequestParam(name = "search", required = false) String search) {
        PageDto<AdminRspdDto> adminEntities = adminService.getAdmins(page, search);
        return ResponseEntity.ok(adminEntities);
    }

    @GetMapping("/admin-accounts/{id}")
    @PreAuthorize("hasAuthority('MANAGE_ADMINS')")
    public ResponseEntity<Object> getAdmin(@PathVariable int id) {
        AdminRspdDto adminEntity = adminService.getAdminById(id);
        return ResponseEntity.ok(adminEntity);
    }

    @GetMapping("/admin-groups")
    @PreAuthorize("hasAuthority('MANAGE_ADMINS')")
    public ResponseEntity<Object> getGroups(@RequestParam(name = "page", required = false) Integer page,
                                            @RequestParam(name = "search", defaultValue = "") String search) {
        // In case page is not provided, it will return all groups
        if (page == null) {
            return ResponseEntity.ok(groupService.getGroups());
        }

        // In case page provided, return paginated groups
        return ResponseEntity.ok(groupService.getGroups(page, search));
    }

    @GetMapping("/admin-groups/{id}")
    @PreAuthorize("hasAuthority('MANAGE_ADMINS')")
    public ResponseEntity<Object> getGroup(@PathVariable int id) {
        return ResponseEntity.ok(groupService.getGroupById(id));
    }

    @GetMapping("/permissions")
    @PreAuthorize("hasAuthority('MANAGE_ADMINS')")
    public ResponseEntity<Object> getPermissions(@RequestParam(name = "page", required = false) Integer page,
                                                 @RequestParam(name = "search", defaultValue = "") String search) {
        if (page == null) {
            return ResponseEntity.ok(permissionService.getAllPermissions());
        }
        return ResponseEntity.ok(permissionService.getPermissions(page, search));
    }

    @GetMapping("/permissions/{id}")
    @PreAuthorize("hasAuthority('MANAGE_ADMINS')")
    public ResponseEntity<Object> getPermission(@PathVariable long id) {
        return ResponseEntity.ok(permissionService.getPermissionById(id));
    }

    @PutMapping("/admin-accounts/attach-group")
    @PreAuthorize("hasAuthority('MANAGE_ADMINS')")
    public ResponseEntity<Object> attachGroup(@RequestBody @Valid GenericAssociationRqstDto requestDto) {
        adminService.associateAdminToGroup(requestDto.getParentId(), requestDto.getDependencyIds());
        return ResponseEntity.created(null).build();
    }

    @PutMapping("/admin-accounts/detach-group")
    @PreAuthorize("hasAuthority('MANAGE_ADMINS')")
    public ResponseEntity<Object> detachGroup(@RequestBody @Valid GenericAssociationRqstDto requestDto) {
        adminService.detachAdminFromGroup(requestDto.getParentId(), requestDto.getDependencyIds());
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/admin-groups/attach-permission")
    @PreAuthorize("hasAuthority('MANAGE_ADMINS')")
    public ResponseEntity<Object> attachPermission(@RequestBody @Valid GenericAssociationRqstDto requestDto) {
        adminService.associatePermissionToGroup(requestDto.getParentId(), requestDto.getDependencyIds());
        return ResponseEntity.created(null).build();
    }

    @PutMapping("/admin-groups/detach-permission")
    @PreAuthorize("hasAuthority('MANAGE_ADMINS')")
    public ResponseEntity<Object> detachPermission(@RequestBody @Valid GenericAssociationRqstDto requestDto) {
        adminService.detachPermissionFromGroup(requestDto.getParentId(), requestDto.getDependencyIds());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/admin-accounts")
    @PreAuthorize("hasAuthority('MANAGE_ADMINS')")
    public ResponseEntity<Object> createNewAdmin(@RequestBody @Valid CreateAdminRqstDto createAdminRqstDto) {
        Integer adminId = adminService.createAdmin(createAdminRqstDto);
        return ResponseEntity.created(null).build();
    }

    @PutMapping("/admin-accounts/disable/{id}")
    @PreAuthorize("hasAuthority('MANAGE_ADMINS')")
    public ResponseEntity<Object> disableAdmin(@PathVariable int id) {
        adminService.disableAdmin(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/admin-accounts/enable/{id}")
    @PreAuthorize("hasAuthority('MANAGE_ADMINS')")
    public ResponseEntity<Object> enableAdmin(@PathVariable int id) {
        adminService.enableAdmin(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/admin-accounts/reset-password")
    @PreAuthorize("hasAuthority('MANAGE_ADMINS')")
    public ResponseEntity<Object> resetPassword(@RequestBody @Valid AdminResetPasswordRqstDto adminResetPasswordRqstDto) {
        adminService.resetPassword(adminResetPasswordRqstDto);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/admin-accounts")
    @PreAuthorize("hasAuthority('MANAGE_ADMINS')")
    public ResponseEntity<Object> updateAdmin(@RequestBody @Valid AdminUpdateRqstDto adminUpdateRqstDto) {
        adminService.updateAdmin(adminUpdateRqstDto);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/admin-groups")
    @PreAuthorize("hasAuthority('MANAGE_ADMINS')")
    public ResponseEntity<Object> createNewGroup(@RequestBody @Valid CreateGroupRqstDto createGroupRqstDto) {
        adminService.newAdminGroup(createGroupRqstDto.getName());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/permissions")
    @PreAuthorize("hasAuthority('MANAGE_ADMINS')")
    public ResponseEntity<Object> createNewPermission(@RequestBody @Valid CreatePermissionDto createPermissionDto) {
        adminService.newAdminPermission(createPermissionDto.getPermissionName());
        return ResponseEntity.ok().build();
    }
}
