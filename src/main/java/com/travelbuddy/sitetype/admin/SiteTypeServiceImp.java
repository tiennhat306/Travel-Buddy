package com.travelbuddy.sitetype.admin;

import com.travelbuddy.aspectsbytype.admin.AspectsByTypeService;
import com.travelbuddy.common.constants.PaginationLimitConstants;
import com.travelbuddy.common.exception.errorresponse.DataAlreadyExistsException;
import com.travelbuddy.common.exception.errorresponse.EnumNotFitException;
import com.travelbuddy.common.exception.errorresponse.NotFoundException;
import com.travelbuddy.common.mapper.PageMapper;
import com.travelbuddy.common.paging.PageDto;
import com.travelbuddy.mapper.SiteTypeMapper;
import com.travelbuddy.persistence.domain.dto.aspectsbytype.AspectsByTypeEditRqstDto;
import com.travelbuddy.persistence.domain.dto.siteservice.GroupedSiteServicesRspnDto;
import com.travelbuddy.persistence.domain.dto.siteservice.ServiceByTypeRspnDto;
import com.travelbuddy.persistence.domain.entity.*;
import com.travelbuddy.persistence.repository.*;
import com.travelbuddy.common.constants.DualStateEnum;
import com.travelbuddy.persistence.domain.dto.sitetype.SiteTypeCreateRqstDto;
import com.travelbuddy.persistence.domain.dto.sitetype.SiteTypeRspnDto;
import com.travelbuddy.servicegroup.admin.ServiceGroupService;
import com.travelbuddy.systemlog.admin.SystemLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class SiteTypeServiceImp implements SiteTypeService {
    private final SiteTypeRepository siteTypeRepository;
    private final ServiceGroupByTypeRepository serviceGroupByTypeRepository;
    private final ServicesByGroupRepository servicesByGroupRepository;
    private final SiteTypeMapper siteTypeMapper;
    private final PageMapper pageMapper;
    private final ServiceGroupRepository serviceGroupRepository;
    private final ServiceGroupService serviceGroupService;
    private final AspectsByTypeRepository aspectsByTypeRepository;
    private final SystemLogService systemLogService;
    private final AspectsByTypeService aspectsByTypeService;

    @Override
    public Integer createSiteType(SiteTypeCreateRqstDto siteTypeCreateRqstDto) {
        DualStateEnum dualState;
        try {
            dualState = DualStateEnum.valueOf(siteTypeCreateRqstDto.getMode());
        } catch (IllegalArgumentException e) {
            throw new EnumNotFitException("Invalid mode: " + siteTypeCreateRqstDto.getMode());
        }

        SiteTypeEntity siteType = SiteTypeEntity.builder()
                .typeName(siteTypeCreateRqstDto.getSiteTypeName())
                .dualState(DualStateEnum.valueOf(siteTypeCreateRqstDto.getMode()))
                .build();

        return siteTypeRepository.save(siteType).getId();
    }

    @Override
    public PageDto<SiteTypeRspnDto> getAllSiteTypes(int page, int limit) {
        Pageable pageable = PageRequest.of(page - 1, limit, Sort.by("typeName"));
        Page<SiteTypeEntity> siteTypes = siteTypeRepository.findAll(pageable);
        return pageMapper.toPageDto(siteTypes.map(siteTypeMapper::siteTypeEntityToSiteTypeRspnDto));
    }

    @Override
    public PageDto<SiteTypeRspnDto> searchSiteTypes(String siteTypeSearch, int page, int limit) {
        Pageable pageable = PageRequest.of(page - 1, limit, Sort.by("typeName"));
        Page<SiteTypeEntity> siteTypes = siteTypeRepository.searchSiteTypeEntitiesByTypeNameContainingIgnoreCase(siteTypeSearch, pageable);
        return pageMapper.toPageDto(siteTypes.map(siteTypeMapper::siteTypeEntityToSiteTypeRspnDto));
    }

    @Override
    public List<GroupedSiteServicesRspnDto> getAssociatedServiceGroups(Integer siteTypeId) {
        List<ServiceGroupByTypeEntity> serviceGroupByTypes = (serviceGroupByTypeRepository.findAllByTypeId(siteTypeId).orElse(new ArrayList<>()));
        List<GroupedSiteServicesRspnDto> groupedSiteServices = new ArrayList<>();
        for (ServiceGroupByTypeEntity serviceGroupByType : serviceGroupByTypes) {
            GroupedSiteServicesRspnDto groupedSiteServicesRspnDtoItem = new GroupedSiteServicesRspnDto();
            List<ServiceEntity> servicesInGroupList = servicesByGroupRepository.findAllByServiceGroupId(serviceGroupByType.getServiceGroupId())
                    .orElseThrow(() -> new NotFoundException("Service group not found"))
                    .stream()
                    .map(ServicesByGroupEntity::getServiceEntity)
                    .toList();
            groupedSiteServicesRspnDtoItem.setServiceGroup(serviceGroupByType.getServiceGroupEntity());
            groupedSiteServicesRspnDtoItem.setServices(servicesInGroupList);
            groupedSiteServices.add(groupedSiteServicesRspnDtoItem);
        }
        return groupedSiteServices;
    }

    @Override
    public List<GroupedSiteServicesRspnDto> getAssociatedServiceGroups() {
        List<ServiceGroupEntity> serviceGroupEntities = serviceGroupRepository.findAll();
        List<GroupedSiteServicesRspnDto> groupedSiteServices = new ArrayList<>();
        for (ServiceGroupEntity serviceGroupEntity : serviceGroupEntities) {
            GroupedSiteServicesRspnDto groupedSiteServicesRspnDtoItem = new GroupedSiteServicesRspnDto();
            List<ServiceEntity> servicesInGroupList = servicesByGroupRepository.findAllByServiceGroupId(serviceGroupEntity.getId())
                    .orElseThrow(() -> new NotFoundException("Service group not found"))
                    .stream()
                    .map(ServicesByGroupEntity::getServiceEntity)
                    .toList();
            groupedSiteServicesRspnDtoItem.setServiceGroup(serviceGroupEntity);
            groupedSiteServicesRspnDtoItem.setServices(servicesInGroupList);
            groupedSiteServices.add(groupedSiteServicesRspnDtoItem);
        }
        return groupedSiteServices;
    }

    @Override
    public void updateSiteType(int siteTypeId, SiteTypeCreateRqstDto siteTypeCreateRqstDto) {
        SiteTypeEntity siteType = siteTypeRepository.findById(siteTypeId)
                .orElseThrow(() -> new NotFoundException("Site type not found"));

//        if (siteTypeRepository.existsByTypeNameIgnoreCase(siteTypeCreateRqstDto.getSiteTypeName()))
//            throw new DataAlreadyExistsException("Site type already exists");
        List<SiteTypeEntity> types = siteTypeRepository.findAllByTypeNameIgnoreCase(siteTypeCreateRqstDto.getSiteTypeName()).orElse(new ArrayList<SiteTypeEntity>());
        if (types.size() != 0 && types.get(0).getId() != siteTypeId) {
            throw new DataAlreadyExistsException("Site type already exists");
        }

        DualStateEnum dualState;
        try {
            dualState = DualStateEnum.valueOf(siteTypeCreateRqstDto.getMode());
        } catch (IllegalArgumentException e) {
            throw new EnumNotFitException("Invalid mode: " + siteTypeCreateRqstDto.getMode());
        }

        siteType.setTypeName(siteTypeCreateRqstDto.getSiteTypeName());
        siteType.setDualState(DualStateEnum.valueOf(siteTypeCreateRqstDto.getMode()));
        siteTypeRepository.save(siteType);
    }

    @Override
    public Integer handlePostSiteType(SiteTypeCreateRqstDto siteTypeCreateRqstDto) {
        if (siteTypeRepository.existsByTypeNameIgnoreCase(siteTypeCreateRqstDto.getSiteTypeName()))
            throw new DataAlreadyExistsException("Site type already exists");

        Integer siteTypeId = createSiteType(siteTypeCreateRqstDto);
        for (Integer serviceGroupId : siteTypeCreateRqstDto.getServiceGroups()) {
            serviceGroupService.associateType(serviceGroupId, siteTypeId);
        }
        List<AspectsByTypeEntity> aspectsByType = new ArrayList<AspectsByTypeEntity>();
        for (String aspectName : siteTypeCreateRqstDto.getAspects()) {
            aspectsByType.add(AspectsByTypeEntity.builder().typeId(siteTypeId).aspectName(aspectName).build());
        }
        aspectsByTypeRepository.saveAll(aspectsByType);
        systemLogService.logInfo("Site type with id " + siteTypeId + " created");
        return siteTypeId;
    }

    @Override
    public void handleUpdateSiteType(int siteTypeId, SiteTypeCreateRqstDto siteTypeCreateRqstDto) {
        updateSiteType(siteTypeId, siteTypeCreateRqstDto);
        systemLogService.logInfo("Site type with id " + siteTypeId + " updated");
    }

    @Override
    public void handlePostAspect(Integer typeId, List<String> aspectNames) {
        aspectsByTypeService.addNewAspects(typeId, aspectNames);
        systemLogService.logInfo("New aspects added to site type with id " + typeId);
    }

    @Override
    public void handleUpdateAspect(AspectsByTypeEditRqstDto aspectsByTypeEditRqstDto) {
        AspectsByTypeEntity aspect = aspectsByTypeRepository.findById(aspectsByTypeEditRqstDto.getId())
                .orElseThrow(() -> new NotFoundException("Aspect not found"));
        List<AspectsByTypeEntity> aspectsByType = aspectsByTypeRepository.findAllByTypeId(aspect.getTypeId())
                .orElseThrow(() -> new NotFoundException("Aspect not found"));
        if (aspectsByType.stream().anyMatch(a -> a.getAspectName().equalsIgnoreCase(aspectsByTypeEditRqstDto.getNewAspectName())))
            throw new DataAlreadyExistsException("Aspect already exists");
        aspectsByTypeService.updateAspectName(aspectsByTypeEditRqstDto.getId(), aspectsByTypeEditRqstDto.getNewAspectName());
        systemLogService.logInfo("Aspect with id " + aspectsByTypeEditRqstDto.getId() + " updated");
    }

    @Override
    public Map<String, Object> handleDeleteAspect(List<Integer> aspectIds) {
        List<AspectsByTypeEntity> aspectsByType = aspectsByTypeService.deleteAspectsByIds(aspectIds);
        Map<String, Object> response = new HashMap<>();
        response.put("failed", aspectsByType);
        systemLogService.logInfo("Aspects with ids " + aspectIds + " deleted");
        return response;
    }

    @Override
    public ServiceByTypeRspnDto handleGetAssociatedServices(Integer siteTypeId) {
        // 1. Get siteType from siteTypeId, check if Not found
        SiteTypeEntity siteType = siteTypeRepository.findById(siteTypeId)
                .orElseThrow(() -> new NotFoundException("Site type not found"));
        List<GroupedSiteServicesRspnDto> groupedSiteServices = getAssociatedServiceGroups(siteTypeId);
        ServiceByTypeRspnDto servicesByTypeRspnDto = new ServiceByTypeRspnDto();
        SiteTypeRspnDto siteTypeRspnDto = new SiteTypeRspnDto(siteType);
        servicesByTypeRspnDto.setSiteType(siteTypeRspnDto);
        servicesByTypeRspnDto.setGroupedSiteServices(groupedSiteServices);
        return servicesByTypeRspnDto;
    }

    @Override
    public PageDto<SiteTypeRspnDto> handleGetSiteTypes(Integer page, Integer limit, String siteTypeSearch) {
        // Check for limit is set
        if (limit == null) {
            limit = PaginationLimitConstants.SITE_TYPE_LIMIT;
        }
        PageDto<SiteTypeRspnDto> siteTypesPage = siteTypeSearch.trim().isEmpty()
                ? getAllSiteTypes(page, limit)
                : searchSiteTypes(siteTypeSearch, page, limit);
        return siteTypesPage;
    }
}
