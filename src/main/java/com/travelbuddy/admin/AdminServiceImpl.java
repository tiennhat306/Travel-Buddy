package com.travelbuddy.admin;

import com.travelbuddy.common.constants.PaginationLimitConstants;
import com.travelbuddy.common.exception.errorresponse.NotFoundException;
import com.travelbuddy.common.mapper.PageMapper;
import com.travelbuddy.common.paging.PageDto;
import com.travelbuddy.mapper.AdminMapper;
import com.travelbuddy.persistence.domain.dto.account.admin.AdminDetailRspnDto;
import com.travelbuddy.persistence.domain.entity.AdminEntity;
import com.travelbuddy.persistence.domain.entity.GroupEntity;
import com.travelbuddy.persistence.repository.AdminRepository;
import com.travelbuddy.persistence.repository.GroupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {
    private final AdminRepository adminRepository;
    private final PageMapper pageMapper;
    private final AdminMapper adminMapper;
    private final GroupRepository groupRepository;

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
    public void associateAdminToGroup(int adminId, long groupId) {
        AdminEntity adminEntity = adminRepository.findById(adminId)
                .orElseThrow(() -> new NotFoundException("Admin with id not found"));
        GroupEntity groupEntity = groupRepository.findById(groupId)
                .orElseThrow(() -> new NotFoundException("Group with id not found"));
        // Associate group to admin
        adminEntity.getGroupEntities().add(groupEntity);
        // Associate admin to group
        groupEntity.getAdminEntities().add(adminEntity);
        adminRepository.save(adminEntity);
    }

    @Override
    public void detachAdminFromGroup(int adminId, long groupId) {
        AdminEntity adminEntity = adminRepository.findById(adminId)
                .orElseThrow(() -> new NotFoundException("Admin with id not found"));
        GroupEntity groupEntity = groupRepository.findById(groupId)
                .orElseThrow(() -> new NotFoundException("Group with id not found"));
        // Detach group from admin
        adminEntity.getGroupEntities().remove(groupEntity);
        // Detach admin from group
        groupEntity.getAdminEntities().remove(adminEntity);
        adminRepository.save(adminEntity);
    }
}
