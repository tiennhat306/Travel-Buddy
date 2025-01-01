package com.travelbuddy.groups.admin;

import com.travelbuddy.common.constants.PaginationLimitConstants;
import com.travelbuddy.common.exception.errorresponse.NotFoundException;
import com.travelbuddy.common.mapper.PageMapper;
import com.travelbuddy.common.paging.PageDto;
import com.travelbuddy.persistence.domain.entity.GroupEntity;
import com.travelbuddy.persistence.repository.GroupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GroupServiceImp implements GroupService {
    private final GroupRepository groupRepository;
    private final PageMapper pageMapper;

    @Override
    public PageDto<GroupEntity> getGroups(int page, String search) {
        Pageable pageable = PageRequest.of(page - 1, PaginationLimitConstants.GROUP_LIMIT, Sort.by("name"));
        Page<GroupEntity> groupEntities = groupRepository.findAllByNameContainingIgnoreCase(search, pageable);
        return pageMapper.toPageDto(groupEntities);
    }

    @Override
    public List<GroupEntity> getGroups() {
        return groupRepository.findAll();
    }

    @Override
    public GroupEntity getGroupById(long id) {
        return groupRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Group with id not found"));
    }

}
