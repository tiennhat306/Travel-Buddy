package com.travelbuddy.auth.controller;

import com.travelbuddy.admin.AdminService;
import com.travelbuddy.common.paging.PageDto;
import com.travelbuddy.groups.admin.GroupService;
import com.travelbuddy.persistence.domain.dto.account.admin.AdminDetailRspnDto;
import com.travelbuddy.persistence.domain.entity.AdminEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
public class AdminController {
    private final AdminService adminService;
    private final GroupService groupService;

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
        PageDto<AdminEntity> adminEntities = adminService.getAdmins(page, search);
        return ResponseEntity.ok(adminEntities);
    }

    @GetMapping("/admin-accounts/{id}")
    @PreAuthorize("hasAuthority('MANAGE_ADMINS')")
    public ResponseEntity<Object> getAdmin(@PathVariable int id) {
        AdminEntity adminEntity = adminService.getAdminById(id);
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
    public ResponseEntity<Object> getPermissions() {
        return ResponseEntity.ok("Permissions");
    }

    @PutMapping("/admin-accounts/attach-group")
    @PreAuthorize("hasAuthority('MANAGE_ADMINS')")
    public ResponseEntity<Object> attachGroup(@RequestParam(name = "adminId") int adminId,
                                              @RequestParam(name = "groupId") int groupId) {
        adminService.associateAdminToGroup(adminId, groupId);
        return ResponseEntity.created(null).build();
    }

    @PutMapping("/admin-accounts/detach-group")
    @PreAuthorize("hasAuthority('MANAGE_ADMINS')")
    public ResponseEntity<Object> detachGroup(@RequestParam(name = "adminId") int adminId,
                                              @RequestParam(name = "groupId") int groupId) {
        adminService.detachAdminFromGroup(adminId, groupId);
        return ResponseEntity.noContent().build();
    }
}
