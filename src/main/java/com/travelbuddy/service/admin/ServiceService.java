package com.travelbuddy.service.admin;

import com.travelbuddy.common.paging.PageDto;
import com.travelbuddy.persistence.domain.dto.service.ServiceCreateRqstDto;
import com.travelbuddy.persistence.domain.dto.siteservice.GroupedSiteServicesRspnDto;
import com.travelbuddy.persistence.domain.entity.ServiceEntity;

import java.security.Provider;
import java.util.List;

public interface ServiceService {
    void createSiteService(ServiceCreateRqstDto serviceCreateRqstDto);
    PageDto<ServiceEntity> getAllSiteServices(int page);
    PageDto<ServiceEntity> searchSiteServices(String serviceSearch, int page);
    void updateSiteService(Integer serviceId, ServiceCreateRqstDto serviceCreateRqstDto);
    void createServicesBySiteVersion(int siteVersionId, int serviceId);
    void createServicesBySiteVersion(int siteVersionId, List<Integer> serviceIds);
    void removeServicesBySiteVersion(int siteVersionId, int serviceId);
    void removeServicesBySiteVersion(int siteVersionId);
    PageDto<ServiceEntity> handleGetSiteServices(String serviceSearch, int page);
    List<GroupedSiteServicesRspnDto> getGroupedSiteServices(int siteVersionId);
}
