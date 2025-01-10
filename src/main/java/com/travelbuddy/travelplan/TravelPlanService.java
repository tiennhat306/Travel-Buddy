package com.travelbuddy.travelplan;

import com.travelbuddy.persistence.domain.dto.travelplan.*;
import com.travelbuddy.upload.cloud.dto.FileRspnDto;

import java.util.List;

public interface TravelPlanService {
    int createTravelPlan(TravelPlanCreateRqstDto travelPlanCreateRqstDto);

    void addMemberToTravelPlan(int travelPlanId, int userId);

    void removeMemberFromTravelPlan(int travelPlanId, int userId);

    void changeMemberRole(int travelPlanId, ChgMemberRoleRqstDto chgMemberRoleRqstDto);

    void addSiteToTravelPlan(int travelPlanId, TravelPlanSiteCreateRqstDto travelPlanSiteCreateRqstDto);

    void removeSiteFromTravelPlan(int travelPlanId, int siteId);

    void updateSiteInTravelPlan(int travelPlanId, TravelPlanSiteUpdateRqstDto travelPlanSiteUpdateRqstDto);

    void deleteTravelPlan(int travelPlanId);

    TravelPlanRspnDto getTravelPlan(int travelPlanId);

    List<TravelPlanBasicRspnDto> getTravelPlans();

    void updateTravelPlan(int travelPlanId, TravelPlanUpdateRqstDto travelPlanUpdateRqstDto);

    void changeCover(int travelPlanId, FileRspnDto uploadedFile);

    void exitTravelPlan(int travelPlanId);
}
