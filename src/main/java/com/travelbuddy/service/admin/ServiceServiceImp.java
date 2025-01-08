package com.travelbuddy.service.admin;

import com.travelbuddy.common.exception.errorresponse.DataAlreadyExistsException;
import com.travelbuddy.common.exception.errorresponse.NotFoundException;
import com.travelbuddy.common.mapper.PageMapper;
import com.travelbuddy.common.paging.PageDto;
import com.travelbuddy.persistence.domain.dto.service.ServiceCreateRqstDto;
import com.travelbuddy.persistence.domain.dto.siteservice.GroupedSiteServicesRspnDto;
import com.travelbuddy.persistence.domain.entity.*;
import com.travelbuddy.persistence.repository.*;
import com.travelbuddy.systemlog.admin.SystemLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Pageable;


import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.travelbuddy.common.constants.PaginationLimitConstants.SITE_SERVICE_LIMIT;

@Service
@RequiredArgsConstructor
public class ServiceServiceImp implements ServiceService {
    private final ServicesBySiteVersionRepository servicesBySiteVersionRepository;
    private final ServiceRepository serviceRepository;
    private final PageMapper pageMapper;
    private final ServiceGroupByTypeRepository serviceGroupByTypeRepository;
    private final SiteVersionRepository siteVersionRepository;
    private final ServicesByGroupRepository servicesByGroupRepository;
    private final SystemLogService systemLogService;

    @Override
    public void createSiteService(ServiceCreateRqstDto serviceCreateRqstDto) {
        if (serviceRepository.existsByServiceNameIgnoreCase(serviceCreateRqstDto.getServiceName()))
            throw new DataAlreadyExistsException("Service already exists");
        ServiceEntity service = ServiceEntity.builder().serviceName(serviceCreateRqstDto.getServiceName()).build();
        systemLogService.logInfo("New site service created" + serviceCreateRqstDto.getServiceName());
        serviceRepository.save(service);
    }

    @Override
    public PageDto<ServiceEntity> getAllSiteServices(int page) {
        Pageable pageable = PageRequest.of(page - 1, SITE_SERVICE_LIMIT, Sort.by("serviceName"));
        Page<ServiceEntity> siteServices = serviceRepository.findAll(pageable);
        return pageMapper.toPageDto(siteServices);
    }

    @Override
    public PageDto<ServiceEntity> searchSiteServices(String serviceSearch, int page) {
        Pageable pageable = PageRequest.of(page - 1, SITE_SERVICE_LIMIT, Sort.by("serviceName"));
        Page<ServiceEntity> siteServices = serviceRepository.searchServiceEntitiesByServiceNameContainingIgnoreCase(serviceSearch, pageable);
        return pageMapper.toPageDto(siteServices);
    }

    @Override
    public void createServicesBySiteVersion(int siteVersionId, int serviceId) {
        ServicesBySiteVersionEntity servicesBySiteVersion = ServicesBySiteVersionEntity.builder().siteVersionId(siteVersionId).serviceId(serviceId).build();
        servicesBySiteVersionRepository.save(servicesBySiteVersion);
    }

    @Override
    public void createServicesBySiteVersion(int siteVersionId, List<Integer> serviceIds) {
        ArrayList<ServicesBySiteVersionEntity> servicesBySiteVersionList = new ArrayList<>();
        for (int serviceId : serviceIds) {
            servicesBySiteVersionList.add(ServicesBySiteVersionEntity.builder().siteVersionId(siteVersionId).serviceId(serviceId).build());
        }
        servicesBySiteVersionRepository.saveAll(servicesBySiteVersionList);
    }

    @Override
    public void removeServicesBySiteVersion(int siteVersionId, int serviceId) {
        servicesBySiteVersionRepository.deleteBySiteVersionIdAndServiceId(siteVersionId, serviceId);
    }

    @Override
    public void removeServicesBySiteVersion(int siteVersionId) {
        servicesBySiteVersionRepository.deleteAllBySiteVersionId(siteVersionId);
    }

    @Override
    public List<GroupedSiteServicesRspnDto> getGroupedSiteServices(int siteVersionId) {
        // 1. Find all services recorded along with site version
        List<ServicesBySiteVersionEntity> existedServices = (servicesBySiteVersionRepository.findAllBySiteVersionId(siteVersionId)).orElse(new ArrayList<>());
        // 2. Find all service groups along with type
        List<ServiceGroupByTypeEntity> serviceGroupByTypes = (serviceGroupByTypeRepository.findAllByTypeId(siteVersionRepository.findById(siteVersionId).orElseThrow().getTypeId())).orElse(new ArrayList<>());
        // 3. Find all services by in each groups
        List<GroupedSiteServicesRspnDto> groupedSiteServices = new ArrayList<>();
        for (ServiceGroupByTypeEntity serviceGroupByType : serviceGroupByTypes) {
            GroupedSiteServicesRspnDto groupedSiteServicesRspnDtoItem = new GroupedSiteServicesRspnDto();
            List<ServiceEntity> servicesInGroupList = (servicesByGroupRepository.findAllByServiceGroupIdAndServiceIdIn(serviceGroupByType.getServiceGroupId(), existedServices.stream().map(ServicesBySiteVersionEntity::getServiceId).toList()).orElse(new ArrayList<>())).stream().map(ServicesByGroupEntity::getServiceEntity).toList();

            // Set values
            groupedSiteServicesRspnDtoItem.setServiceGroup(serviceGroupByType.getServiceGroupEntity());
            groupedSiteServicesRspnDtoItem.setServices(servicesInGroupList);
            if (!servicesInGroupList.isEmpty()) groupedSiteServices.add(groupedSiteServicesRspnDtoItem);
        }
        return groupedSiteServices;
    }

    @Override
    public void updateSiteService(Integer serviceId, ServiceCreateRqstDto serviceCreateRqstDto) {
        Optional<ServiceEntity> service = serviceRepository.findById(serviceId);
        if (service.isEmpty()) {
            throw new NotFoundException("Service not found");
        }
        if (serviceRepository.existsByServiceNameIgnoreCase(serviceCreateRqstDto.getServiceName())) {
            throw new DataAlreadyExistsException("Service already exists");
        }
        service.get().setServiceName(serviceCreateRqstDto.getServiceName());
        serviceRepository.save(service.get());
        systemLogService.logInfo("Site service updated" + serviceCreateRqstDto.getServiceName());
    }

    @Override
    public PageDto<ServiceEntity> handleGetSiteServices(String serviceSearch, int page) {
        if (page < 1) {
            page = 1;
        }
        return serviceSearch.trim().isEmpty()
                ? getAllSiteServices(page)
                : searchSiteServices(serviceSearch, page);
    }
}
