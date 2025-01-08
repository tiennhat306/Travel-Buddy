package com.travelbuddy.servicegroup.admin;
import com.travelbuddy.persistence.domain.dto.servicegroup.ServiceGroupCreateRqstDto;
import com.travelbuddy.persistence.domain.dto.siteservice.GroupedSiteServicesRspnDto;
import com.travelbuddy.persistence.domain.entity.ServiceGroupEntity;

import java.util.List;

public interface ServiceGroupService {
    Integer createServiceGroup(ServiceGroupCreateRqstDto serviceGroupCreateRqstDto);
    GroupedSiteServicesRspnDto getServiceGroup(Integer id);
    void updateServiceGroup(Integer serviceGroupId, ServiceGroupCreateRqstDto serviceGroupCreateRqstDto);

    void associateService(Integer serviceGroupId, Integer serviceId);
    void detachService(Integer serviceGroupId, Integer serviceId);
    void detachService(Integer id);

    void associateType(Integer serviceGroupId, Integer typeId);
    void detachType(Integer serviceGroupId, Integer typeId);
    void detachType(Integer id);

    void handleAssociateService(Integer serviceGroupId, Integer serviceId, Boolean remove);
    void handleAssociateType(Integer serviceGroupId, Integer typeId, Boolean remove);
    void handleUpdateServiceGroup(Integer serviceGroupId, ServiceGroupCreateRqstDto serviceGroupCreateRqstDto);
    Integer handleCreateServiceGroup(ServiceGroupCreateRqstDto serviceGroupCreateRqstDto);
}
