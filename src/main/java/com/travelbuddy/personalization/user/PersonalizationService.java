package com.travelbuddy.personalization.user;

import com.travelbuddy.persistence.domain.dto.personalization.AssociationSiteTypeAndSiteDto;
import com.travelbuddy.persistence.domain.dto.personalization.InitiationPersonalizationDto;
import com.travelbuddy.persistence.domain.entity.SiteEntity;

import java.util.List;

public interface PersonalizationService {
    List<AssociationSiteTypeAndSiteDto> getAssociationSiteTypeAndSiteDto();
    InitiationPersonalizationDto getChoices();
    Object submitChoices(Integer userId, List<Integer> choices);
}
