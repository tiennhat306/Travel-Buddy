package com.travelbuddy.siteversion.user;

import com.travelbuddy.common.paging.PageDto;
import com.travelbuddy.persistence.domain.dto.site.*;

import java.util.List;

public interface SiteVersionService {
    int createSiteVersion(SiteCreateRqstDto siteCreateRqstDto, int siteId);
    SiteRepresentationDto getSiteView(Integer siteId, Integer ownerId);
    SiteRepresentationDto getSiteVersionView(Integer siteVersionId, Integer ownerId);
    List<MapRepresentationDto> getSitesInRange(double lat, double lng, double degRadius);
    Integer updateSiteVersion(SiteUpdateRqstDto siteUpdateRqstDto);
    SiteBasicInfoRspnDto getSiteVersionBasicView(Integer siteVersionId);
    PageDto<SiteStatusRspndDto> getSiteStatuses(int page, int userId);
    SiteRepresentationDto adminGetValidSiteRepresention(int siteId);
}
