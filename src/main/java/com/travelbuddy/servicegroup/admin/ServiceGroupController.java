package com.travelbuddy.servicegroup.admin;

import com.travelbuddy.persistence.domain.dto.servicegroup.ServiceGroupCreateRqstDto;
import com.travelbuddy.persistence.domain.dto.siteservice.GroupedSiteServicesRspnDto;
import com.travelbuddy.persistence.domain.entity.ServiceGroupEntity;
import com.travelbuddy.persistence.repository.ServiceRepository;
import com.travelbuddy.sitetype.admin.SiteTypeService;
import com.travelbuddy.systemlog.admin.SystemLogService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.travelbuddy.common.paging.PageDto;
import java.net.URI;
import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/service-groups")
public class ServiceGroupController {
    private final ServiceGroupService serviceGroupService;
    private final SiteTypeService siteTypeService;

    @PreAuthorize("hasAuthority('MANAGE_SITE_TYPES')")
    @PostMapping
    public ResponseEntity<Object> createServiceGroup(@RequestBody @Valid ServiceGroupCreateRqstDto serviceCreateRqstDto) {
        Integer id = serviceGroupService.handleCreateServiceGroup(serviceCreateRqstDto);
        return ResponseEntity.ok(URI.create("/api/admin/service-groups/" + id));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getServiceGroupById(@PathVariable Integer id, @RequestParam(required = false) Boolean associate) {
        GroupedSiteServicesRspnDto serviceGroupEntity = serviceGroupService.getServiceGroup(id);
        if (associate != null && associate) {
            return ResponseEntity.ok(serviceGroupEntity);
        }
        return ResponseEntity.ok(serviceGroupEntity.getServiceGroup());
    }


    @GetMapping()
    public ResponseEntity<Object> getServiceGroups() {
        List<GroupedSiteServicesRspnDto> serviceGroupEntities = siteTypeService.getAssociatedServiceGroups();
        return ResponseEntity.ok(serviceGroupEntities);
    }

    @PreAuthorize("hasAuthority('MANAGE_SITE_TYPES')")
    @PutMapping("/{id}")
    public ResponseEntity<Object> updateServiceGroup(@PathVariable Integer id, @RequestBody @Valid ServiceGroupCreateRqstDto serviceCreateRqstDto) {
        serviceGroupService.handleUpdateServiceGroup(id, serviceCreateRqstDto);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasAuthority('MANAGE_SITE_TYPES')")
    @PutMapping("/associate-service")
    public ResponseEntity<Object> associateService(@RequestParam Integer id, @RequestParam(required = false) Boolean remove, @RequestParam(required = false) Integer serviceId) {
        serviceGroupService.handleAssociateService(id, serviceId, remove);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasAuthority('MANAGE_SITE_TYPES')")
    @PutMapping("/associate-type")
    public ResponseEntity<Object> associateType(@RequestParam Integer id, @RequestParam(required = false) Boolean remove, @RequestParam(required = false) Integer typeId) {
        serviceGroupService.handleAssociateType(id, typeId, remove);
        return ResponseEntity.ok().build();
    }


}
