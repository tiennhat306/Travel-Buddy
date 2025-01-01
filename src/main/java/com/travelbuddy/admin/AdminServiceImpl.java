package com.travelbuddy.admin;

import com.travelbuddy.common.constants.PaginationLimitConstants;
import com.travelbuddy.common.exception.errorresponse.NotFoundException;
import com.travelbuddy.common.mapper.PageMapper;
import com.travelbuddy.common.paging.PageDto;
import com.travelbuddy.groups.admin.GroupService;
import com.travelbuddy.mapper.AdminMapper;
import com.travelbuddy.persistence.domain.dto.account.admin.AdminDetailRspnDto;
import com.travelbuddy.persistence.domain.dto.account.admin.AdminResetPasswordRqstDto;
import com.travelbuddy.persistence.domain.dto.account.admin.AdminUpdateRqstDto;
import com.travelbuddy.persistence.domain.dto.account.admin.CreateAdminRqstDto;
import com.travelbuddy.persistence.domain.entity.AdminEntity;
import com.travelbuddy.persistence.domain.entity.FileEntity;
import com.travelbuddy.persistence.domain.entity.GroupEntity;
import com.travelbuddy.persistence.domain.entity.PermissionEntity;
import com.travelbuddy.persistence.repository.AdminRepository;
import com.travelbuddy.persistence.repository.GroupRepository;
import com.travelbuddy.persistence.repository.PermissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {
    private final AdminRepository adminRepository;
    private final PageMapper pageMapper;
    private final GroupRepository groupRepository;
    private final PermissionRepository permissionRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public int getAdminIdByEmail(String email) {
        return adminRepository
                .findByEmail(email)
                .orElseThrow(() -> new NotFoundException("Admin with email not found"))
                .getId();
    }

    @Override
    public PageDto<AdminEntity> getAdmins(int page, String search) {
        Pageable pageable = PageRequest.of(page - 1, PaginationLimitConstants.ADMIN_ACCOUNT_LIMITS, Sort.by("id"));
        Page<AdminEntity> adminEntities = adminRepository.findAllByEmailContaining(search, pageable);
        return pageMapper.toPageDto(adminEntities);
    }

    public AdminEntity getAdminById(int id) {
        return adminRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Admin with id not found"));
    }

    @Override
    public void associateAdminToGroup(int adminId, List<Integer> groupId) {
        AdminEntity adminEntity = adminRepository.findById(adminId)
                .orElseThrow(() -> new NotFoundException("Admin with id not found"));
        List<GroupEntity> groupEntities = new ArrayList<GroupEntity>();
        for (int id : groupId) {
            GroupEntity groupEntity = groupRepository.findById((long) id)
                    .orElseThrow(() -> new NotFoundException("Group with id not found"));
            groupEntities.add(groupEntity);
        }
        // Associate group to admin
        adminEntity.getGroupEntities().addAll(groupEntities);
        adminRepository.save(adminEntity);
    }

    @Override
    public void detachAdminFromGroup(int adminId, List<Integer> groupId) {
        AdminEntity adminEntity = adminRepository.findById(adminId)
                .orElseThrow(() -> new NotFoundException("Admin with id not found"));
        List<GroupEntity> groupEntities = new ArrayList<GroupEntity>();
        for (int id : groupId) {
            GroupEntity groupEntity = groupRepository.findById((long) id)
                    .orElseThrow(() -> new NotFoundException("Group with id not found"));
            groupEntities.add(groupEntity);
        }
        // Detach group from admin
        adminEntity.getGroupEntities().removeAll(groupEntities);
        adminRepository.save(adminEntity);
    }

    @Override
    public void associatePermissionToGroup(long groupId, List<Integer> permissionId) {
        GroupEntity groupEntity = groupRepository.findById(groupId).orElseThrow(() -> new NotFoundException("Group with id not found"));

        List<PermissionEntity> permissionEntities = new ArrayList<PermissionEntity>();
        for (int id : permissionId) {
            PermissionEntity permissionEntity = permissionRepository.findById((long) id).orElseThrow(() -> new NotFoundException("Permission with id not found"));
            permissionEntities.add(permissionEntity);
        }

        groupEntity.getPermissionEntities().addAll(permissionEntities);
        groupRepository.save(groupEntity);
    }

    @Override
    public void detachPermissionFromGroup(long groupId, List<Integer> permissionId) {
        GroupEntity groupEntity = groupRepository.findById(groupId).orElseThrow(() -> new NotFoundException("Group with id not found"));

        List<PermissionEntity> permissionEntities = new ArrayList<PermissionEntity>();
        for (int id : permissionId) {
            PermissionEntity permissionEntity = permissionRepository.findById((long) id).orElseThrow(() -> new NotFoundException("Permission with id not found"));
            permissionEntities.add(permissionEntity);
        }

        groupEntity.getPermissionEntities().removeAll(permissionEntities);
        groupRepository.save(groupEntity);
    }

    @Override
    public Integer createAdmin(CreateAdminRqstDto createAdminRqstDto) {
        AdminEntity adminEntity = createAdminRqstDto.mapToEntity();
        adminEntity.setPassword(passwordEncoder.encode(createAdminRqstDto.getPassword()));
        return adminRepository.save(adminEntity).getId();
    }

    @Override
    public void updateAdmin(AdminUpdateRqstDto adminUpdateRqstDto) {
        AdminEntity adminEntity = adminRepository.findById(adminUpdateRqstDto.getId())
                .orElseThrow(() -> new NotFoundException("Admin with id not found"));
        adminEntity.setFullName(adminUpdateRqstDto.getFullName());
        adminEntity.setGender(adminUpdateRqstDto.getGender());
        adminEntity.setAddress(adminUpdateRqstDto.getAddress());
        adminEntity.setPhoneNumber(adminUpdateRqstDto.getPhoneNumber());
        adminEntity.setEnabled(adminUpdateRqstDto.isEnabled());
        adminEntity.setPassword(passwordEncoder.encode(adminUpdateRqstDto.getPassword()));
        adminEntity.setAvatar(FileEntity.builder().id(adminUpdateRqstDto.getAvatarId()).url(adminUpdateRqstDto.getAvatarUrl()).build());
        adminRepository.save(adminEntity);
    }

    @Override
    public void disableAdmin(int id) {
        AdminEntity admin = adminRepository.findById(id).orElseThrow(() -> new NotFoundException("Not found admin"));
        admin.setEnabled(false);
        adminRepository.save(admin);
    }

    @Override
    public void enableAdmin(int id) {
        AdminEntity admin = adminRepository.findById(id).orElseThrow(() -> new NotFoundException("Not found admin"));
        admin.setEnabled(true);
        adminRepository.save(admin);
    }

    @Override
    public void resetPassword(AdminResetPasswordRqstDto adminResetPasswordRqstDto) {
        AdminEntity admin = adminRepository.findById(adminResetPasswordRqstDto.getAdminId()).orElseThrow(() -> new NotFoundException("Not found admin"));
        admin.setPassword(passwordEncoder.encode(adminResetPasswordRqstDto.getPassword()));
        adminRepository.save(admin);
    }
}
