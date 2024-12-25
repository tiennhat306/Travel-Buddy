package com.travelbuddy.servicegroup.admin;

import com.travelbuddy.persistence.domain.dto.servicegroup.ServiceGroupCreateRqstDto;
import com.travelbuddy.persistence.domain.dto.siteservice.GroupedSiteServicesRspnDto;
import com.travelbuddy.persistence.domain.entity.ServiceGroupEntity;
import com.travelbuddy.persistence.repository.ServiceRepository;
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
    private final SystemLogService systemLogService;

    @PreAuthorize("hasAuthority('MANAGE_SITE_TYPES')")
    @PostMapping
    public ResponseEntity<Object> createServiceGroup(@RequestBody @Valid ServiceGroupCreateRqstDto serviceCreateRqstDto) {
        Integer id = serviceGroupService.createServiceGroup(serviceCreateRqstDto);
        systemLogService.logInfo("Service group " + id + " created");
        return ResponseEntity.ok(URI.create("/api/admin/service-groups/" + id));
    }

    @PreAuthorize("hasAuthority('MANAGE_SITE_TYPES')")
    @GetMapping("/{id}")
    public ResponseEntity<Object> getServiceGroup(@PathVariable Integer id) {
        ServiceGroupEntity serviceGroupEntity = serviceGroupService.getServiceGroup(id);
        return ResponseEntity.ok(serviceGroupEntity);
    }

    @GetMapping()
    public ResponseEntity<Object> getServiceGroups() {
        List<GroupedSiteServicesRspnDto> serviceGroupEntities = serviceGroupService.getServiceGroups();
        return ResponseEntity.ok(serviceGroupEntities);
    }

    @PreAuthorize("hasAuthority('MANAGE_SITE_TYPES')")
    @PutMapping("/{id}")
    public ResponseEntity<Object> updateServiceGroup(@PathVariable Integer id, @RequestBody @Valid ServiceGroupCreateRqstDto serviceCreateRqstDto) {
        serviceGroupService.updateServiceGroup(id, serviceCreateRqstDto);
        systemLogService.logInfo("Service group " + id + " updated");
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasAuthority('MANAGE_SITE_TYPES')")
    @PutMapping("/associate-service")
    public ResponseEntity<Object> associateService(@RequestParam Integer id, @RequestParam(required = false) Boolean remove, @RequestParam(required = false) Integer serviceId) {
        /*
        * The id is required, if removed is not set, automatically set to false
        * If the removed is set to true, serviceId is not required
        * If the removed is set to false, serviceId is required
        * */
        if (id == null) {
            throw new IllegalArgumentException("id is required");
        }
        // 1. Check if remove is set
        if (remove == null || !remove) {
            remove = false;
            if (serviceId == null) {
                throw new IllegalArgumentException("serviceId is required when remove is set to false");
            }
            serviceGroupService.associateService(id, serviceId);
        } else {
            remove = true;
            serviceGroupService.detachService(id);
        }
        systemLogService.logInfo("Service group " + id + " updated");
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasAuthority('MANAGE_SITE_TYPES')")
    @PutMapping("/associate-type")
    public ResponseEntity<Object> associateType(@RequestParam Integer id, @RequestParam(required = false) Boolean remove, @RequestParam(required = false) Integer typeId) {
        /*
        * The id is required, if removed is not set, automatically set to false
        * If the removed is set to true, typeId is not required
        * If the removed is set to false, typeId is required
        * */
        if (id == null) {
            throw new IllegalArgumentException("id is required");
        }
        // 1. Check if remove is set
        if (remove == null || !remove) {
            remove = false;
            if (typeId == null) {
                throw new IllegalArgumentException("typeId is required when remove is set to false");
            }
            serviceGroupService.associateType(id, typeId);
        } else {
            remove = true;
            if (typeId != null) {
                serviceGroupService.detachType(id, typeId);
            } else {
                serviceGroupService.detachType(id);
            }
        }
        systemLogService.logInfo("Service group " + id + " updated");
        return ResponseEntity.ok().build();
    }
}
