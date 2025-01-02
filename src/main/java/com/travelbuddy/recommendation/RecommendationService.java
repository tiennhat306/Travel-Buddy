package com.travelbuddy.recommendation;

public interface RecommendationService {
    Object getRecommendations(int page);

    Object getDiscoverRecommendations(DiscoverSiteTypeRqstDto discoverSiteTypeRqstDto);

    Object getRecommendationsForPlan(int planId, int page);
}
