package com.travelbuddy.admin;

import com.travelbuddy.common.constants.PaginationLimitConstants;
import com.travelbuddy.common.exception.errorresponse.NotFoundException;
import com.travelbuddy.common.mapper.PageMapper;
import com.travelbuddy.common.paging.PageDto;
import com.travelbuddy.mapper.AdminMapper;
import com.travelbuddy.persistence.domain.dto.account.admin.AdminDetailRspnDto;
import com.travelbuddy.persistence.domain.entity.AdminEntity;
import com.travelbuddy.persistence.repository.AdminRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {
    private final AdminRepository adminRepository;
    private final PageMapper pageMapper;
    private final AdminMapper adminMapper;

    @Override
    public int getAdminIdByEmail(String email) {
        return adminRepository
                .findByEmail(email)
                .orElseThrow(() -> new NotFoundException("Admin with email not found"))
                .getId();
    }

    @Override
    public PageDto<AdminDetailRspnDto> getAdmins(int page, String search) {
        Pageable pageable = PageRequest.of(page - 1, PaginationLimitConstants.ADMIN_ACCOUNT_LIMITS, Sort.by("id"));
        Page<AdminEntity> adminEntities = adminRepository.findAllByEmailContaining(search, pageable);
        return pageMapper.toPageDto(adminEntities.map(adminMapper::toAdminDetailRspnDto));
    }
}
