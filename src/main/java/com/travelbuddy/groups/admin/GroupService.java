package com.travelbuddy.groups.admin;

import com.travelbuddy.common.paging.PageDto;
import com.travelbuddy.persistence.domain.entity.GroupEntity;

import java.util.List;

public interface GroupService {
    PageDto<GroupEntity> getGroups(int page, String search);
    List<GroupEntity> getGroups();
    GroupEntity getGroupById(long id);
}
