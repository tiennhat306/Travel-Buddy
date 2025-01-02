package com.travelbuddy.site.user;

import com.travelbuddy.common.paging.PageDto;
import com.travelbuddy.persistence.domain.dto.site.*;

import java.util.List;

public interface SiteService {
    Integer createSiteWithSiteVersion(SiteCreateRqstDto siteCreateRqstDto);
    Integer updateSite(SiteUpdateRqstDto siteUpdateRqstDto);
    SiteBasicInfoRspnDto getSiteBasicRepresentation(Integer siteID);

    void likeSite(int siteId);

    void dislikeSite(int siteId);

    PageDto<SiteBasicInfoRspnDto> searchSites(String siteSearch, int page);

    PageDto<SiteBasicInfoRspnDto> discoverSites(int page);

    List<SiteBasicInfoRspnDto> getSiteBasicRepresentationByType(List<Integer> typeIds);
}