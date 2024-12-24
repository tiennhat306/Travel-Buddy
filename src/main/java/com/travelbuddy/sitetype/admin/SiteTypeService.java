package com.travelbuddy.sitetype.admin;

import com.travelbuddy.common.paging.PageDto;
import com.travelbuddy.persistence.domain.dto.siteservice.GroupedSiteServicesRspnDto;
import com.travelbuddy.persistence.domain.dto.sitetype.SiteTypeCreateRqstDto;
import com.travelbuddy.persistence.domain.dto.sitetype.SiteTypeRspnDto;
import com.travelbuddy.persistence.domain.entity.ServiceEntity;

import java.util.List;

public interface SiteTypeService {
    Integer createSiteType(SiteTypeCreateRqstDto siteTypeCreateRqstDto);
    void updateSiteType(int siteTypeId, SiteTypeCreateRqstDto siteTypeCreateRqstDto);
    PageDto<SiteTypeRspnDto> getAllSiteTypes(int page, int limit);
    PageDto<SiteTypeRspnDto> searchSiteTypes(String siteTypeSearch, int page, int limit);
    List<GroupedSiteServicesRspnDto> getAssociatedServiceGroups(Integer siteTypeId);
    List<GroupedSiteServicesRspnDto> getAssociatedServiceGroups();
}

