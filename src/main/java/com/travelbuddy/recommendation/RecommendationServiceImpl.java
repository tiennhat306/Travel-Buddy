package com.travelbuddy.recommendation;

import com.travelbuddy.common.exception.errorresponse.NotFoundException;
import com.travelbuddy.common.paging.PageDto;
import com.travelbuddy.common.utils.RequestUtils;
import com.travelbuddy.persistence.domain.dto.site.SiteBasicInfoRspnDto;
import com.travelbuddy.persistence.domain.entity.TravelPlanEntity;
import com.travelbuddy.persistence.domain.entity.TravelPlanUserEntity;
import com.travelbuddy.persistence.domain.entity.UserEntity;
import com.travelbuddy.persistence.repository.TravelPlanRepository;
import com.travelbuddy.persistence.repository.TravelPlanUserRepository;
import com.travelbuddy.site.user.SiteService;
import com.travelbuddy.travelplan.TravelPlanService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

import static com.travelbuddy.common.constants.ExternalLinkConstant.AI_SERVER_URL;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecommendationServiceImpl implements RecommendationService {
    private final SiteService siteService;
    private final RequestUtils requestUtils;
    private final RestTemplate restTemplate;
    private final TravelPlanRepository travelPlanRepository;
    private final TravelPlanUserRepository travelPlanUserRepository;

    @Override
    public Object getRecommendations(int page) {
        int userId = requestUtils.getUserIdCurrentRequest();
        String apiUrl = AI_SERVER_URL + "recommend/" + userId + "?top_k=" + String.valueOf(page * 10);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> requestEntity = new HttpEntity<>(headers);
        String response = makeRequest(apiUrl, requestEntity);
        if (response == null) {
            return new PageDto<>(new ArrayList<>(), null);
        }
        JSONArray recommendations = new JSONArray(response);

        List<SiteBasicInfoRspnDto> sites = new ArrayList<>();
        for (int i = (page - 1) * 10; i < recommendations.length(); i++) {
            JSONObject recommendation = recommendations.getJSONObject(i);
            int siteId = recommendation.getInt("placeId");
            try {
                SiteBasicInfoRspnDto site = siteService.getSiteBasicRepresentation(siteId);
                sites.add(site);
            } catch (Exception e) {
                log.error("Failed to get site with id {}", siteId, e);
            }
        }

        return new PageDto<>(sites, null);
    }

    @Override
    public Object getDiscoverRecommendations(DiscoverSiteTypeRqstDto discoverSiteTypeRqstDto) {
        List<Integer> typeIds = discoverSiteTypeRqstDto.getTypeIds();

        List<SiteBasicInfoRspnDto> sitesByType = siteService.getSiteBasicRepresentationByType(typeIds);

        return new PageDto<>(sitesByType, null);
    }

    @Override
    public Object getRecommendationsForPlan(int planId, int page) {
        TravelPlanEntity travelPlanEntity = travelPlanRepository.findById(planId).orElseThrow(() -> new NotFoundException("Plan not found"));
        TravelPlanUserEntity travelPlanUserEntity = travelPlanUserRepository.findOwnerByTravelPlanId(planId).orElseThrow(() -> new NotFoundException("Owner not found"));
        UserEntity owner = travelPlanUserEntity.getId().getUserEntity();
        int userId = owner.getId();
        String apiUrl = AI_SERVER_URL + "recommend/" + userId + "?top_k=" + String.valueOf(page * 10);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> requestEntity = new HttpEntity<>(headers);
        String response = makeRequest(apiUrl, requestEntity);
        JSONArray recommendations = new JSONArray(response);

        List<SiteBasicInfoRspnDto> sites = new ArrayList<>();
        for (int i = (page - 1) * 10; i < recommendations.length(); i++) {
            JSONObject recommendation = recommendations.getJSONObject(i);
            int siteId = recommendation.getInt("placeId");
            try {
                SiteBasicInfoRspnDto site = siteService.getSiteBasicRepresentation(siteId);
                sites.add(site);
            } catch (Exception e) {
                log.error("Failed to get site with id {}", siteId, e);
            }
        }

        return new PageDto<>(sites, null);
    }

    private String makeRequest(String apiUrl, HttpEntity<String> requestEntity) {
        try {
            ResponseEntity<String> response = restTemplate.exchange(apiUrl, HttpMethod.GET, requestEntity, String.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                return response.getBody();
            } else {
                log.error("Request failed " + response.getBody());
                return null;
            }
        } catch (Exception e) {
            log.error("Request failed {}", e.getMessage(), e);
            return null;
        }
    }
}
