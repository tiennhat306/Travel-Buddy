package com.travelbuddy.servicegroup.admin;

import com.travelbuddy.common.exception.errorresponse.DataAlreadyExistsException;
import com.travelbuddy.common.exception.errorresponse.NotFoundException;
import com.travelbuddy.persistence.domain.dto.servicegroup.ServiceGroupCreateRqstDto;
import com.travelbuddy.persistence.domain.dto.siteservice.GroupedSiteServicesRspnDto;
import com.travelbuddy.persistence.domain.entity.ServiceGroupByTypeEntity;
import com.travelbuddy.persistence.domain.entity.ServiceGroupEntity;
import com.travelbuddy.persistence.domain.entity.ServicesByGroupEntity;
import com.travelbuddy.persistence.repository.*;

import com.travelbuddy.sitetype.admin.SiteTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ServiceGroupServiceImp implements ServiceGroupService {
    private final ServiceGroupRepository serviceGroupRepository;
    private final ServicesByGroupRepository servicesByGroupRepository;
    private final ServiceGroupByTypeRepository serviceGroupByTypeRepository;
    private final SiteTypeService siteTypeService;
    private final SiteTypeRepository siteTypeRepository;


    @Override
    public void updateServiceGroup(Integer serviceGroupId, ServiceGroupCreateRqstDto serviceGroupCreateRqstDto) {
        ServiceGroupEntity serviceGroupEntity = serviceGroupRepository.findById(serviceGroupId).orElseThrow(() -> new NotFoundException("Service group with id " + serviceGroupId + " not found"));
        if (serviceGroupRepository.existsByServiceGroupNameIgnoreCase(serviceGroupCreateRqstDto.getGroupName())) {
            throw new DataAlreadyExistsException("Service group with name " + serviceGroupCreateRqstDto.getGroupName() + " already exists");
        }
        serviceGroupEntity.setServiceGroupName(serviceGroupCreateRqstDto.getGroupName());
        serviceGroupRepository.save(serviceGroupEntity);
    }

    @Override
    public Integer createServiceGroup(ServiceGroupCreateRqstDto serviceGroupCreateRqstDto) {
        if (serviceGroupRepository.existsByServiceGroupNameIgnoreCase(serviceGroupCreateRqstDto.getGroupName())) {
            throw new DataAlreadyExistsException("Service group with name " + serviceGroupCreateRqstDto.getGroupName() + " already exists");
        }
        ServiceGroupEntity serviceGroupEntity = ServiceGroupEntity.builder()
                .serviceGroupName(serviceGroupCreateRqstDto.getGroupName())
                .build();
        return serviceGroupRepository.save(serviceGroupEntity).getId();
    }

    @Override
    public ServiceGroupEntity getServiceGroup(Integer id) {
        return serviceGroupRepository.findById(id).orElseThrow(() -> new NotFoundException("Service group with id " + id + " not found"));
    }

    @Override
    public List<GroupedSiteServicesRspnDto> getServiceGroups() {
        return siteTypeService.getAssociatedServiceGroups();
    }

    @Override
    public void detachService(Integer id) {
        // Remove the record of the service group with the given id
        // Check the existence of the service group with the given id
        if (!servicesByGroupRepository.existsById(id)) {
            throw new NotFoundException("Service group with id " + id + " not found");
        }
        servicesByGroupRepository.deleteById(id);
    }

    @Override
    public void detachService(Integer serviceGroupId, Integer serviceId) {
        // Remove the record of the service group with the given id
        // Check the existence of the service group with the given id
        ServicesByGroupEntity servicesByGroupEntity = servicesByGroupRepository.findByServiceGroupIdAndServiceId(serviceGroupId, serviceId).orElseThrow(() -> new NotFoundException("Service group with id " + serviceGroupId + " not found"));
        servicesByGroupRepository.delete(servicesByGroupEntity);
    }

    @Override
    public void associateService(Integer serviceGroupId, Integer serviceId) {
        // Check the existence of the service group with the given id
        if (!serviceGroupRepository.existsById(serviceGroupId)) {
            throw new NotFoundException("Service group with id " + serviceGroupId + " not found");
        }
        // Check the existence of the type with the given id
        if (!siteTypeRepository.existsById(serviceId)) {
            throw new NotFoundException("Service with id " + serviceId + " not found");
        }
        // Check if the service group with the given id is already associated with the service with the given id
        if (servicesByGroupRepository.findByServiceGroupIdAndServiceId(serviceGroupId, serviceId).isPresent()) {
            throw new DataAlreadyExistsException("Service group with id " + serviceGroupId + " is already associated with service with id " + serviceId);
        }
        ServicesByGroupEntity servicesByGroupEntity = ServicesByGroupEntity.builder()
                .serviceGroupId(serviceGroupId)
                .serviceId(serviceId)
                .build();
        servicesByGroupRepository.save(servicesByGroupEntity);
    }

    @Override
    public void associateType(Integer serviceGroupId, Integer typeId) {
        // Check the existence of the service group with the given id
        if (!serviceGroupRepository.existsById(serviceGroupId)) {
            throw new NotFoundException("Service group with id " + serviceGroupId + " not found");
        }
        // Check the existence of the type with the given id
        if (!siteTypeRepository.existsById(typeId)) {
            throw new NotFoundException("Type with id " + typeId + " not found");
        }
        // Check if the service group with the given id is already associated with the type with the given id
        if (serviceGroupByTypeRepository.findByTypeIdAndServiceGroupId(typeId, serviceGroupId).isPresent()) {
            throw new DataAlreadyExistsException("Service group with id " + serviceGroupId + " is already associated with type with id " + typeId);
        }
        ServiceGroupByTypeEntity serviceGroupByTypeEntity = ServiceGroupByTypeEntity.builder()
                .serviceGroupId(serviceGroupId)
                .typeId(typeId)
                .build();
        serviceGroupByTypeRepository.save(serviceGroupByTypeEntity);
    }

    @Override
    public void detachType(Integer id) {
        // Remove the record of the service group with the given id
        // Check the existence of the service group with the given id
        if (!serviceGroupByTypeRepository.existsById(id)) {
            throw new NotFoundException("Service group with id " + id + " not found");
        }
        serviceGroupByTypeRepository.deleteById(id);
    }

    @Override
    public void detachType(Integer serviceGroupId, Integer typeId) {
        // Remove the record of the service group with the given id
        // Check the existence of the service group with the given id
        ServiceGroupByTypeEntity serviceGroupByTypeEntity = serviceGroupByTypeRepository.findByTypeIdAndServiceGroupId(typeId, serviceGroupId).orElseThrow(() -> new NotFoundException("Service group with id " + serviceGroupId + " not found"));
        serviceGroupByTypeRepository.delete(serviceGroupByTypeEntity);
    }
}
