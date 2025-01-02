package com.travelbuddy.recommendation;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public ResponseEntity<Object> getDiscoverRecommendations(@RequestParam List<Integer> typeIds) {
        return ResponseEntity.ok(recommendationService.getDiscoverRecommendations(typeIds));
    }

    @GetMapping("/for-plan/{planId}")
    public ResponseEntity<Object> getRecommendationsForPlan(@PathVariable int planId,
                                                            @RequestParam(name = "page", required = false, defaultValue = "1") int page) {
        return ResponseEntity.ok(recommendationService.getRecommendationsForPlan(planId, page));
    }
}
