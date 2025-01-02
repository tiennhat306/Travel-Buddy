package com.travelbuddy.recommendation;

import java.util.List;

public interface RecommendationService {
    Object getRecommendations(int page);

    Object getDiscoverRecommendations(List<Integer> typeIds);

    Object getRecommendationsForPlan(int planId, int page);
}
