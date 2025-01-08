package com.travelbuddy.sitetype.admin;

import com.travelbuddy.aspectsbytype.admin.AspectsByTypeService;
import com.travelbuddy.common.exception.errorresponse.DataAlreadyExistsException;
import com.travelbuddy.common.exception.errorresponse.NotFoundException;
import com.travelbuddy.persistence.domain.dto.aspectsbytype.AspectsByTypeCreateRqstDto;
import com.travelbuddy.persistence.domain.dto.aspectsbytype.AspectsByTypeEditRqstDto;
import com.travelbuddy.persistence.domain.entity.AspectsByTypeEntity;
import com.travelbuddy.persistence.repository.AspectsByTypeRepository;
import com.travelbuddy.persistence.repository.SiteTypeRepository;
import com.travelbuddy.persistence.domain.dto.sitetype.SiteTypeCreateRqstDto;
import com.travelbuddy.servicegroup.admin.ServiceGroupService;
import com.travelbuddy.systemlog.admin.SystemLogService;
import jakarta.validation.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/site-types")
public class SiteTypeController {
    private final SiteTypeService siteTypeService;

    @PreAuthorize("hasAuthority('MANAGE_SITE_TYPES')")
    @PostMapping
    public ResponseEntity<Object> postSiteType(@RequestBody @Valid SiteTypeCreateRqstDto siteTypeCreateRqstDto) {
        Integer siteTypeId = siteTypeService.handlePostSiteType(siteTypeCreateRqstDto);
        return ResponseEntity.created(URI.create("/admin/api/siteTypes/" + siteTypeId)).build();
    }

    @PreAuthorize("hasAuthority('MANAGE_SITE_TYPES')")
    @PutMapping("/{siteTypeId}")
    public ResponseEntity<Object> updateSiteType(@PathVariable int siteTypeId, @RequestBody @Valid SiteTypeCreateRqstDto siteTypeCreateRqstDto) {
        siteTypeService.handleUpdateSiteType(siteTypeId, siteTypeCreateRqstDto);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAuthority('MANAGE_SITE_TYPES')")
    @PostMapping("/aspects")
    public ResponseEntity<Object> postAspect(@RequestBody @Valid AspectsByTypeCreateRqstDto aspectsByTypeCreateRqstDto) {
        siteTypeService.handlePostAspect(aspectsByTypeCreateRqstDto.getTypeId(), aspectsByTypeCreateRqstDto.getAspectNames());
        return ResponseEntity.created(URI.create("/admin/api/siteTypes/aspects")).build();
    }

    @PreAuthorize("hasAuthority('MANAGE_SITE_TYPES')")
    @PutMapping("/aspects")
    public ResponseEntity<Object> updateAspect(@RequestBody @Valid AspectsByTypeEditRqstDto aspectsByTypeEditRqstDto) {
        siteTypeService.handleUpdateAspect(aspectsByTypeEditRqstDto);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAuthority('MANAGE_SITE_TYPES')")
    @DeleteMapping("/aspects")
    public ResponseEntity<Object> deleteAspect(@RequestBody List<Integer> aspectIds) {
        Map<String, Object> response = siteTypeService.handleDeleteAspect(aspectIds);
        return ResponseEntity.ok(response);
    }
}
