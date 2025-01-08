package com.travelbuddy.sitetype.user;

import com.travelbuddy.aspectsbytype.admin.AspectsByTypeService;
import com.travelbuddy.common.constants.PaginationLimitConstants;
import com.travelbuddy.common.exception.errorresponse.NotFoundException;
import com.travelbuddy.common.paging.PageDto;
import com.travelbuddy.persistence.domain.dto.aspectsbytype.AspectsByTypeRepresentationRspndDto;
import com.travelbuddy.persistence.domain.dto.siteservice.GroupedSiteServicesRspnDto;
import com.travelbuddy.persistence.domain.dto.siteservice.ServiceByTypeRspnDto;
import com.travelbuddy.persistence.domain.dto.sitetype.SiteTypeRspnDto;
import com.travelbuddy.persistence.domain.entity.SiteTypeEntity;
import com.travelbuddy.persistence.repository.SiteTypeRepository;
import com.travelbuddy.service.admin.ServiceService;
import com.travelbuddy.sitetype.admin.SiteTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController("SiteTypeControllerUser")
@RequiredArgsConstructor
@RequestMapping("/api/site-types")
public class SiteTypeController {
    private final SiteTypeService siteTypeService;
    private final SiteTypeRepository siteTypeRepository;
    private final AspectsByTypeService aspectsByTypeService;

    @GetMapping("/{siteTypeId}/services")
    public ResponseEntity<Object> getAssociatedServices(@PathVariable Integer siteTypeId) {
        ServiceByTypeRspnDto servicesByTypeRspnDto = siteTypeService.handleGetAssociatedServices(siteTypeId);
        return ResponseEntity.ok(servicesByTypeRspnDto);
    }

    @GetMapping("/{siteTypeId}/aspects")
    public ResponseEntity<Object> getAssociatedAspects(@PathVariable Integer siteTypeId) {
        List< AspectsByTypeRepresentationRspndDto> aspects = aspectsByTypeService.getAspectsByTypeId(siteTypeId);
        return ResponseEntity.ok(aspects);
    }

    @GetMapping
    public ResponseEntity<Object> getSiteTypes(@RequestParam(name = "q", required = false, defaultValue = "") String siteTypeSearch,
                                               @RequestParam(name = "page", required = false, defaultValue = "1") int page,
                                               @RequestParam(name = "limit", required = false) Integer limit) {

        PageDto<SiteTypeRspnDto> siteTypesPage = siteTypeService.handleGetSiteTypes(page, limit, siteTypeSearch);

        return ResponseEntity.ok(siteTypesPage);
    }

}