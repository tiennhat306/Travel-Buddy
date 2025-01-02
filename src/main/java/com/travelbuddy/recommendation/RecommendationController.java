package com.travelbuddy.recommendation;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/recommendations")
@RequiredArgsConstructor
public class RecommendationController {
    private final RecommendationService recommendationService;


    @GetMapping("/for-you")
    public ResponseEntity<Object> getRecommendations( @RequestParam(name = "page", required = false, defaultValue = "1") int page) {
        return ResponseEntity.ok(recommendationService.getRecommendations(page));
    }

    @GetMapping("/discover")
    public ResponseEntity<Object> getDiscoverRecommendations(@RequestBody DiscoverSiteTypeRqstDto discoverSiteTypeRqstDto) {
        return ResponseEntity.ok(recommendationService.getDiscoverRecommendations(discoverSiteTypeRqstDto));
    }

    @GetMapping("/for-plan/{planId}")
    public ResponseEntity<Object> getRecommendationsForPlan(@PathVariable int planId,
                                                            @RequestParam(name = "page", required = false, defaultValue = "1") int page) {
        return ResponseEntity.ok(recommendationService.getRecommendationsForPlan(planId, page));
    }
}
