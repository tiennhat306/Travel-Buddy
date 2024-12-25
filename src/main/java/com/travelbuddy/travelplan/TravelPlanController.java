package com.travelbuddy.travelplan;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.travelbuddy.common.utils.FilenameUtils;
import com.travelbuddy.persistence.domain.dto.travelplan.*;
import com.travelbuddy.systemlog.admin.SystemLogService;
import com.travelbuddy.upload.cloud.StorageService;
import com.travelbuddy.upload.cloud.dto.FileRspnDto;
import com.travelbuddy.upload.cloud.dto.FileUploadRqstDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/travel-plans")
@RequiredArgsConstructor
public class TravelPlanController {
    private final TravelPlanService travelPlanService;
    private final StorageService storageService;
    private final ObjectMapper objectMapper;
    private final SystemLogService systemLogService;

    @PostMapping
    public ResponseEntity<Void> createTravelPlan(@RequestBody @Valid TravelPlanCreateRqstDto travelPlanCreateRqstDto) {
        int travelPlanId =  travelPlanService.createTravelPlan(travelPlanCreateRqstDto);
        systemLogService.logInfo("Travel plan " + travelPlanId + " created");
        return ResponseEntity.created(URI.create("/api/travel-plans/" + travelPlanId)).build();
    }

    @PutMapping("/{travelPlanId}")
    public ResponseEntity<Void> updateTravelPlan(@PathVariable int travelPlanId, @RequestBody @Valid TravelPlanUpdateRqstDto travelPlanUpdateRqstDto) {
        travelPlanService.updateTravelPlan(travelPlanId, travelPlanUpdateRqstDto);

        return ResponseEntity.ok().build();
    }

    @PutMapping("/{travelPlanId}/change-cover")
    public ResponseEntity<Object> changeCover(@PathVariable int travelPlanId, @RequestParam("cover") MultipartFile cover) throws IOException {
        if (ObjectUtils.isEmpty(cover)) {
            throw new IllegalArgumentException("Cover  is required");
        }

        String mimeType = cover.getContentType();
        if (StringUtils.isEmpty(mimeType) || !mimeType.startsWith("image")) {
            throw new IllegalArgumentException("Avatar must be an image");
        }

        FileUploadRqstDto fileUploadRqstDto = FileUploadRqstDto.builder()
                .inputStream(cover.getInputStream())
                .folder("covers")
                .mimeType(mimeType)
                .extension(FilenameUtils.getExtension(cover.getOriginalFilename()).orElse(null))
                .build();

        FileRspnDto uploadedFile =  storageService.uploadFile(fileUploadRqstDto);

        travelPlanService.changeCover(travelPlanId, uploadedFile);

        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("coverUrl", uploadedFile.getUrl());
        systemLogService.logInfo("Travel plan " + travelPlanId + " changed");
        return ResponseEntity.ok(objectNode);
    }

    @PutMapping("/{travelPlanId}/add-member")
    public ResponseEntity<Void> addMemberToTravelPlan(@PathVariable int travelPlanId, @RequestBody @Valid TravelPlanMemberRqstDto travelPlanMemberRqstDto) {
        travelPlanService.addMemberToTravelPlan(travelPlanId, travelPlanMemberRqstDto.getUserId());
        systemLogService.logInfo("User " + travelPlanMemberRqstDto.getUserId() + " added to travel plan " + travelPlanId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{travelPlanId}/remove-member")
    public ResponseEntity<Void> removeMemberFromTravelPlan(@PathVariable int travelPlanId, @RequestBody @Valid TravelPlanMemberRqstDto travelPlanMemberRqstDto) {
        travelPlanService.removeMemberFromTravelPlan(travelPlanId, travelPlanMemberRqstDto.getUserId());
        systemLogService.logInfo("User " + travelPlanMemberRqstDto.getUserId() + " removed from travel plan " + travelPlanId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{travelPlanId}/change-role")
    public ResponseEntity<Void> changeMemberRole(@PathVariable int travelPlanId, @RequestBody ChgMemberRoleRqstDto chgMemberRoleRqstDto) {
        travelPlanService.changeMemberRole(travelPlanId, chgMemberRoleRqstDto);
        systemLogService.logInfo("User " + chgMemberRoleRqstDto.getUserId() + " role changed in travel plan " + travelPlanId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{travelPlanId}/add-site")
    public ResponseEntity<Void> addSiteToTravelPlan(@PathVariable int travelPlanId, @RequestBody TravelPlanSiteCreateRqstDto travelPlanSiteCreateRqstDto) {
        travelPlanService.addSiteToTravelPlan(travelPlanId, travelPlanSiteCreateRqstDto);
        systemLogService.logInfo("Site " + travelPlanSiteCreateRqstDto.getSiteId() + " added to travel plan " + travelPlanId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{travelPlanId}/remove-site")
    public ResponseEntity<Void> removeSiteFromTravelPlan(@PathVariable int travelPlanId, @RequestBody TravelPlanSiteDeleteRqstDto travelPlanSiteDeleteRqstDto) {
        travelPlanService.removeSiteFromTravelPlan(travelPlanId, travelPlanSiteDeleteRqstDto.getSiteId());
        systemLogService.logInfo("Site " + travelPlanSiteDeleteRqstDto.getSiteId() + " removed from travel plan " + travelPlanId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{travelPlanId}/update-site")
    public ResponseEntity<Void> updateSiteInTravelPlan(@PathVariable int travelPlanId, @RequestBody TravelPlanSiteUpdateRqstDto travelPlanSiteUpdateRqstDto) {
        travelPlanService.updateSiteInTravelPlan(travelPlanId, travelPlanSiteUpdateRqstDto);
        systemLogService.logInfo("Site " + travelPlanSiteUpdateRqstDto.getSiteId() + " updated in travel plan " + travelPlanId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{travelPlanId}")
    public ResponseEntity<Void> deleteTravelPlan(@PathVariable int travelPlanId) {
        travelPlanService.deleteTravelPlan(travelPlanId);
        systemLogService.logInfo("Travel plan " + travelPlanId + " deleted");
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{travelPlanId}")
    public ResponseEntity<Object> getTravelPlan(@PathVariable int travelPlanId) {
        TravelPlanRspnDto travelPlan = travelPlanService.getTravelPlan(travelPlanId);
        return ResponseEntity.ok(travelPlan);
    }

    @GetMapping
    public ResponseEntity<Object> getTravelPlans() {
        List<TravelPlanBasicRspnDto> travelPlans = travelPlanService.getTravelPlans();

        return ResponseEntity.ok(travelPlans);
    }
}
