package com.travelbuddy.service.admin;

import com.travelbuddy.persistence.domain.dto.service.ServiceCreateRqstDto;
import com.travelbuddy.persistence.domain.entity.ServiceEntity;
import com.travelbuddy.systemlog.admin.SystemLogService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.travelbuddy.common.paging.PageDto;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/site-services")
public class ServiceController {
    private final ServiceService serviceService;
    private final SystemLogService systemLogService;

    @PreAuthorize("hasAuthority('MANAGE_SITE_TYPES')")
    @PostMapping
    public ResponseEntity<Object> createSiteService(@RequestBody @Valid ServiceCreateRqstDto serviceCreateRqstDto) {
        serviceService.createSiteService(serviceCreateRqstDto);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<Object> getSiteServices(@RequestParam(name = "q", required = false, defaultValue = "") String serviceSearch,
                                                  @RequestParam(name = "page", required = false, defaultValue = "1") int page) {
        PageDto<ServiceEntity> siteServicesPage = serviceService.handleGetSiteServices(serviceSearch, page);
        return ResponseEntity.ok(siteServicesPage);
    }

    @PreAuthorize("hasAuthority('MANAGE_SITE_TYPES')")
    @PutMapping("/{serviceId}")
    public ResponseEntity<Object> updateSiteService(@PathVariable Integer serviceId, @RequestBody @Valid ServiceCreateRqstDto serviceCreateRqstDto) {
        serviceService.updateSiteService(serviceId, serviceCreateRqstDto);
        return ResponseEntity.noContent().build();
    }
}
