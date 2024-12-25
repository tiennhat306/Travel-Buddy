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
    private final SiteTypeRepository siteTypeRepository;
    private final AspectsByTypeService aspectsByTypeService;
    private final AspectsByTypeRepository aspectsByTypeRepository;
    private final ServiceGroupService serviceGroupService;
    private final SystemLogService systemLogService;

    @PreAuthorize("hasAuthority('MANAGE_SITE_TYPES')")
    @PostMapping
    public ResponseEntity<Object> postSiteType(@RequestBody @Valid SiteTypeCreateRqstDto siteTypeCreateRqstDto) {
        if (siteTypeRepository.existsByTypeNameIgnoreCase(siteTypeCreateRqstDto.getSiteTypeName()))
            throw new DataAlreadyExistsException("Site type already exists");

        Integer siteTypeId = siteTypeService.createSiteType(siteTypeCreateRqstDto);
        for (Integer serviceGroupId : siteTypeCreateRqstDto.getServiceGroups()) {
            serviceGroupService.associateType(serviceGroupId, siteTypeId);
        }
        List<AspectsByTypeEntity> aspectsByType = new ArrayList<AspectsByTypeEntity>();
        for (String aspectName : siteTypeCreateRqstDto.getAspects()) {
            aspectsByType.add(AspectsByTypeEntity.builder().typeId(siteTypeId).aspectName(aspectName).build());
        }
        aspectsByTypeRepository.saveAll(aspectsByType);
        systemLogService.logInfo("Site type with id " + siteTypeId + " created");
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
        systemLogService.logInfo("Site type with id " + siteTypeId + " updated");
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAuthority('MANAGE_SITE_TYPES')")
    @PostMapping("/aspects")
    public ResponseEntity<Object> postAspect(@RequestBody @Valid AspectsByTypeCreateRqstDto aspectsByTypeCreateRqstDto) {
        aspectsByTypeService.addNewAspects(aspectsByTypeCreateRqstDto.getTypeId(), aspectsByTypeCreateRqstDto.getAspectNames());
        systemLogService.logInfo("New aspects added to site type with id " + aspectsByTypeCreateRqstDto.getTypeId());
        return ResponseEntity.created(URI.create("/admin/api/siteTypes/aspects")).build();
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
        systemLogService.logInfo("Aspect with id " + aspectsByTypeEditRqstDto.getId() + " updated");
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAuthority('MANAGE_SITE_TYPES')")
    @DeleteMapping("/aspects")
    public ResponseEntity<Object> deleteAspect(@RequestBody List<Integer> aspectIds) {
        List<AspectsByTypeEntity> aspectsByType = aspectsByTypeService.deleteAspectsByIds(aspectIds);
        Map<String, Object> response = new HashMap<>();
        response.put("failed", aspectsByType);
        systemLogService.logInfo("Aspects with ids " + aspectIds + " deleted");
        return ResponseEntity.ok(response);
    }
}
