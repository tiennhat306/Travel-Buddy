package com.travelbuddy.auth.controller;

import com.travelbuddy.admin.AdminService;
import com.travelbuddy.common.paging.PageDto;
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

    @GetMapping("/accounts")
    @PreAuthorize("hasAuthority('MANAGE_ADMINS')")
    public ResponseEntity<Object> getAccounts(@RequestParam(name = "page", defaultValue = "1") int page,
                                              @RequestParam(name = "search", required = false) String search) {
        PageDto<AdminDetailRspnDto> adminEntities = adminService.getAdmins(page, search);
        return ResponseEntity.ok(adminEntities);
    }

}
