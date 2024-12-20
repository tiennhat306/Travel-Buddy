package com.travelbuddy.sitetype.admin;

import com.travelbuddy.aspectsbytype.admin.AspectsByTypeService;
import com.travelbuddy.common.constants.PaginationLimitConstants;
import com.travelbuddy.common.exception.errorresponse.DataAlreadyExistsException;
import com.travelbuddy.common.exception.errorresponse.NotFoundException;
import com.travelbuddy.common.paging.PageDto;
import com.travelbuddy.persistence.domain.dto.aspectsbytype.AspectsByTypeCreateRqstDto;
import com.travelbuddy.persistence.domain.dto.aspectsbytype.AspectsByTypeEditRqstDto;
import com.travelbuddy.persistence.domain.entity.AspectsByTypeEntity;
import com.travelbuddy.persistence.domain.entity.SiteTypeEntity;
import com.travelbuddy.persistence.repository.AspectsByTypeRepository;
import com.travelbuddy.persistence.repository.SiteTypeRepository;
import com.travelbuddy.persistence.domain.dto.sitetype.SiteTypeCreateRqstDto;
import com.travelbuddy.persistence.domain.dto.sitetype.SiteTypeRspnDto;
import com.travelbuddy.servicegroup.admin.ServiceGroupService;
import jakarta.validation.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/site-types")
public class SiteTypeController {
    private final SiteTypeService siteTypeService;
    private final SiteTypeRepository siteTypeRepository;
    private final AspectsByTypeService aspectsByTypeService;
    private final AspectsByTypeRepository aspectsByTypeRepository;
    private final ServiceGroupService serviceGroupService;

    @PreAuthorize("hasAuthority('MANAGE_SITE_TYPES')")
    @PostMapping
    public ResponseEntity<Object> postSiteType(@RequestBody @Valid SiteTypeCreateRqstDto siteTypeCreateRqstDto) {
        if (siteTypeRepository.existsByTypeNameIgnoreCase(siteTypeCreateRqstDto.getSiteTypeName()))
            throw new DataAlreadyExistsException("Site type already exists");

        Integer siteTypeId = siteTypeService.createSiteType(siteTypeCreateRqstDto);
        for (Integer serviceGroupId : siteTypeCreateRqstDto.getServiceGroups()) {
            serviceGroupService.associateType(serviceGroupId, siteTypeId);
        }
        return ResponseEntity.created(URI.create("/admin/api/siteTypes/" + siteTypeId)).build();
    }

    @GetMapping
    public ResponseEntity<Object> getSiteTypes(@RequestParam(name = "q", required = false, defaultValue = "") String siteTypeSearch,
                                               @RequestParam(name = "page", required = false, defaultValue = "1") int page,
                                               @RequestParam(name = "limit", required = false) Integer limit) {

        // Check for limit is set
        if (limit == null) {
            limit = PaginationLimitConstants.SITE_TYPE_LIMIT;
        }
        PageDto<SiteTypeRspnDto> siteTypesPage = siteTypeSearch.trim().isEmpty()
                ? siteTypeService.getAllSiteTypes(page, limit)
                : siteTypeService.searchSiteTypes(siteTypeSearch, page, limit);

        return ResponseEntity.ok(siteTypesPage);
    }

    @PreAuthorize("hasAuthority('MANAGE_SITE_TYPES')")
    @PutMapping("/{siteTypeId}")
    public ResponseEntity<Object> updateSiteType(@PathVariable int siteTypeId, @RequestBody @Valid SiteTypeCreateRqstDto siteTypeCreateRqstDto) {
        siteTypeService.updateSiteType(siteTypeId, siteTypeCreateRqstDto);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAuthority('MANAGE_SITE_TYPES')")
    @PostMapping("/aspects")
    public ResponseEntity<Object> postAspect(@RequestBody @Valid AspectsByTypeCreateRqstDto aspectsByTypeCreateRqstDto) {
        Integer newAspectByType = aspectsByTypeService.addNewAspect(aspectsByTypeCreateRqstDto.getTypeId(), aspectsByTypeCreateRqstDto.getAspectName());
        return ResponseEntity.created(URI.create("/admin/api/site-types/aspects/" + newAspectByType)).build();
    }

    @PreAuthorize("hasAuthority('MANAGE_SITE_TYPES')")
    @PutMapping("/aspects")
    public ResponseEntity<Object> updateAspect(@RequestBody @Valid AspectsByTypeEditRqstDto aspectsByTypeEditRqstDto) {
        AspectsByTypeEntity aspect = aspectsByTypeRepository.findById(aspectsByTypeEditRqstDto.getId())
                .orElseThrow(() -> new NotFoundException("Aspect not found"));
        List<AspectsByTypeEntity> aspectsByType = aspectsByTypeRepository.findAllByTypeId(aspect.getTypeId())
                .orElseThrow(() -> new NotFoundException("Aspect not found"));
        if (aspectsByType.stream().anyMatch(a -> a.getAspectName().equalsIgnoreCase(aspectsByTypeEditRqstDto.getNewAspectName())))
            throw new DataAlreadyExistsException("Aspect already exists");
        aspectsByTypeService.updateAspectName(aspectsByTypeEditRqstDto.getId(), aspectsByTypeEditRqstDto.getNewAspectName());
        return ResponseEntity.noContent().build();
    }
}
