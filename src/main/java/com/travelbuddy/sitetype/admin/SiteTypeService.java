package com.travelbuddy.sitetype.admin;

import com.travelbuddy.common.paging.PageDto;
import com.travelbuddy.persistence.domain.dto.aspectsbytype.AspectsByTypeEditRqstDto;
import com.travelbuddy.persistence.domain.dto.siteservice.GroupedSiteServicesRspnDto;
import com.travelbuddy.persistence.domain.dto.siteservice.ServiceByTypeRspnDto;
import com.travelbuddy.persistence.domain.dto.sitetype.SiteTypeCreateRqstDto;
import com.travelbuddy.persistence.domain.dto.sitetype.SiteTypeRspnDto;
import com.travelbuddy.persistence.domain.entity.ServiceEntity;

import java.util.List;
import java.util.Map;

public interface SiteTypeService {
    Integer createSiteType(SiteTypeCreateRqstDto siteTypeCreateRqstDto);
    void updateSiteType(int siteTypeId, SiteTypeCreateRqstDto siteTypeCreateRqstDto);
    PageDto<SiteTypeRspnDto> getAllSiteTypes(int page, int limit);
    PageDto<SiteTypeRspnDto> searchSiteTypes(String siteTypeSearch, int page, int limit);
    List<GroupedSiteServicesRspnDto> getAssociatedServiceGroups(Integer siteTypeId);
    List<GroupedSiteServicesRspnDto> getAssociatedServiceGroups();

    Integer handlePostSiteType(SiteTypeCreateRqstDto siteTypeCreateRqstDto);
    void handleUpdateSiteType(int siteTypeId, SiteTypeCreateRqstDto siteTypeCreateRqstDto);
    void handlePostAspect(Integer typeId, List<String> aspectNames);
    void handleUpdateAspect(AspectsByTypeEditRqstDto aspectsByTypeEditRqstDto);
    Map<String, Object> handleDeleteAspect(List<Integer> aspectIds);
    ServiceByTypeRspnDto handleGetAssociatedServices(Integer siteTypeId);
    PageDto<SiteTypeRspnDto> handleGetSiteTypes(Integer page, Integer limit, String siteTypeSearch);
}

