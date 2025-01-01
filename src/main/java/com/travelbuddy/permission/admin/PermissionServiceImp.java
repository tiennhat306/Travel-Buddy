package com.travelbuddy.permission.admin;

import com.travelbuddy.common.exception.errorresponse.NotFoundException;
import com.travelbuddy.common.mapper.PageMapper;
import com.travelbuddy.common.paging.PageDto;
import com.travelbuddy.persistence.domain.entity.PermissionEntity;
import com.travelbuddy.persistence.repository.PermissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.travelbuddy.common.constants.PaginationLimitConstants.PERMISSION_LIMIT;

@Service
@RequiredArgsConstructor
public class PermissionServiceImp implements PermissionService {
    private final PermissionRepository permissionRepository;
    private final PageMapper pageMapper;

    @Override
    public PageDto<PermissionEntity> getPermissions(int page, String search) {
        Pageable pageable = PageRequest.of(page - 1, PERMISSION_LIMIT, Sort.by("id"));
        return pageMapper.toPageDto(permissionRepository.findAllByNameContainingIgnoreCase(search, pageable));
    }

    @Override
    public List<PermissionEntity> getAllPermissions() {
        return permissionRepository.findAll();
    }

    @Override
    public PermissionEntity getPermissionById(long id) {
        return permissionRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Permission with id not found"));
    }
}
