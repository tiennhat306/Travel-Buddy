package com.travelbuddy.sitetype.admin;

import com.travelbuddy.common.exception.errorresponse.DataAlreadyExistsException;
import com.travelbuddy.common.exception.errorresponse.EnumNotFitException;
import com.travelbuddy.common.exception.errorresponse.NotFoundException;
import com.travelbuddy.common.mapper.PageMapper;
import com.travelbuddy.common.paging.PageDto;
import com.travelbuddy.mapper.SiteTypeMapper;
import com.travelbuddy.persistence.domain.dto.siteservice.GroupedSiteServicesRspnDto;
import com.travelbuddy.persistence.domain.entity.*;
import com.travelbuddy.persistence.repository.ServiceGroupByTypeRepository;
import com.travelbuddy.persistence.repository.ServiceGroupRepository;
import com.travelbuddy.persistence.repository.ServicesByGroupRepository;
import com.travelbuddy.persistence.repository.SiteTypeRepository;
import com.travelbuddy.common.constants.DualStateEnum;
import com.travelbuddy.persistence.domain.dto.sitetype.SiteTypeCreateRqstDto;
import com.travelbuddy.persistence.domain.dto.sitetype.SiteTypeRspnDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static com.travelbuddy.common.constants.PaginationLimitConstants.SITE_TYPE_LIMIT;

@Service
@RequiredArgsConstructor
public class SiteTypeServiceImp implements SiteTypeService {
    private final SiteTypeRepository siteTypeRepository;
    private final ServiceGroupByTypeRepository serviceGroupByTypeRepository;
    private final ServicesByGroupRepository servicesByGroupRepository;
    private final SiteTypeMapper siteTypeMapper;
    private final PageMapper pageMapper;
    private final ServiceGroupRepository serviceGroupRepository;

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
}
