package com.travelbuddy.admin;

import com.travelbuddy.common.paging.PageDto;
import com.travelbuddy.persistence.domain.dto.account.admin.AdminDetailRspnDto;
import com.travelbuddy.persistence.domain.entity.AdminEntity;

public interface AdminService {

    int getAdminIdByEmail(String username);
    PageDto<AdminDetailRspnDto> getAdmins(int page, String search);
}
