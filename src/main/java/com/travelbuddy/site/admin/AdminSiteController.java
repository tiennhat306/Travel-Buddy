package com.travelbuddy.site.admin;

import com.travelbuddy.persistence.domain.dto.site.SiteRepresentationDto;
import com.travelbuddy.persistence.repository.SiteApprovalRepository;
import com.travelbuddy.persistence.repository.SiteRepository;
import com.travelbuddy.siteversion.user.SiteVersionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/sites")
@RequiredArgsConstructor
public class AdminSiteController {
    private final SiteVersionService siteVersionService;
    private final SiteApprovalRepository siteApprovalRepository;

    @PreAuthorize("hasAuthority('MANAGE_SITES')")
    @GetMapping
    public ResponseEntity<Object> getSiteVersion(@RequestParam(name = "version") int siteVersionId) {
        // 1. Get the site representation
        SiteRepresentationDto siteRepresentationDto = siteVersionService.getSiteVersionView(siteVersionId, -1);

        return ResponseEntity.ok(siteRepresentationDto);
    }

    @PreAuthorize("hasAuthority('MANAGE_SITES')")
    @GetMapping("/{siteId}")
    public ResponseEntity<Object> getValidSiteRepresention(@PathVariable int siteId) {
        SiteRepresentationDto representationDto = siteVersionService.adminGetValidSiteRepresention(siteId);
        return ResponseEntity.ok(representationDto);
    }
}
