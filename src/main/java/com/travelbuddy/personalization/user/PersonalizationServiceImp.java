package com.travelbuddy.personalization.user;

import com.travelbuddy.persistence.domain.dto.personalization.AssociationSiteTypeAndSiteDto;
import com.travelbuddy.persistence.domain.dto.personalization.CreatePersonalizeRqstDto;
import com.travelbuddy.persistence.domain.dto.personalization.InitiationPersonalizationDto;
import com.travelbuddy.persistence.domain.entity.SiteEntity;
import com.travelbuddy.persistence.domain.entity.SiteMediaEntity;
import com.travelbuddy.persistence.domain.entity.SiteVersionEntity;
import com.travelbuddy.persistence.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

import static com.travelbuddy.common.constants.ExternalLinkConstant.AI_SERVER_URL;
import static com.travelbuddy.common.constants.PaginationLimitConstants.PERSONALIZATION_LIMIT;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class PersonalizationServiceImp implements PersonalizationService {
    private final SiteRepository siteRepository;
    private final SiteVersionRepository siteVersionRepository;
    private final SiteApprovalRepository siteApprovalRepository;
    private final SiteMediaRepository siteMediaRepository;
    private final RestTemplate restTemplate;

    public List<AssociationSiteTypeAndSiteDto> getAssociationSiteTypeAndSiteDto() {
        List<SiteEntity> allSites = siteRepository.findAll();
        Set<Integer> uniqueSiteTypeIds = new HashSet<>();
        Integer siteVersionId = 0;
        Integer siteTypeId = 0;
        List<AssociationSiteTypeAndSiteDto> associationSiteTypeAndSiteDtos = new ArrayList<>();
        Collections.shuffle(allSites);
        for (SiteEntity site : allSites) {
            siteVersionId = siteApprovalRepository.findLatestApprovedSiteVersionIdBySiteId(site.getId()).orElse(null);
            if (siteVersionId == null) {
                continue;
            }

            SiteVersionEntity siteVersion = siteVersionRepository.findById(siteVersionId).orElse(null);
            if (siteVersion == null) {
                continue;
            }
            SiteMediaEntity siteMedia = siteMediaRepository.findFirstBySiteIdAndMediaType(siteVersionId, "IMAGE");
            if (siteMedia == null) {
                continue;
            }
            siteTypeId = siteVersion.getSiteType().getId();
            // If the site type is already added, then skip it
            if (uniqueSiteTypeIds.contains(siteTypeId)) {
                continue;
            }
            uniqueSiteTypeIds.add(siteTypeId);
            associationSiteTypeAndSiteDtos.add(new AssociationSiteTypeAndSiteDto(siteTypeId, site.getId(), siteVersionId, siteVersion.getSiteName(), siteMedia.getMedia().getUrl()));
            if (uniqueSiteTypeIds.size() == PERSONALIZATION_LIMIT) {
                break;
            }
        }
        return associationSiteTypeAndSiteDtos;
    }

    @Override
    public InitiationPersonalizationDto getChoices() {
        List<AssociationSiteTypeAndSiteDto> associationSiteTypeAndSiteDtos = getAssociationSiteTypeAndSiteDto();
        InitiationPersonalizationDto initiationPersonalizationDto = new InitiationPersonalizationDto();
        initiationPersonalizationDto.setChoices(associationSiteTypeAndSiteDtos);
        return initiationPersonalizationDto;
    }

    @Override
    public Object submitChoices(Integer userId, List<Integer> choices) {
        CreatePersonalizeRqstDto createPersonalizeRqstDto = new CreatePersonalizeRqstDto(2, choices);
        return postToAiServer(createPersonalizeRqstDto);
    }

    public Object postToAiServer(CreatePersonalizeRqstDto createPersonalizeRqstDto) {
        String apiUrl = AI_SERVER_URL + "add_user/";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<CreatePersonalizeRqstDto> requestEntity = new HttpEntity<>(createPersonalizeRqstDto, headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(apiUrl, HttpMethod.POST, requestEntity, String.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                return response.getBody();
            } else {
                log.error("Request failed {}", response.getBody());
                throw new RuntimeException("Request failed " + response.getBody());
            }
        } catch (Exception e) {
            log.error("Request failed {}", e.getMessage(), e);
            throw new RuntimeException("Request failed " + e.getMessage());
        }
    }
}
