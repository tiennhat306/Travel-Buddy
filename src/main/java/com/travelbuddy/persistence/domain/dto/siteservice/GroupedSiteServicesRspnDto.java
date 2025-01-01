package com.travelbuddy.persistence.domain.dto.siteservice;

import com.travelbuddy.persistence.domain.entity.ServiceEntity;
import com.travelbuddy.persistence.domain.entity.ServiceGroupEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class GroupedSiteServicesRspnDto {
    private ServiceGroupEntity serviceGroup;
    private List<ServiceEntity> services;
}
