package com.travelbuddy.persistence.domain.dto.personalization;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class InitiationPersonalizationDto {
    private List<AssociationSiteTypeAndSiteDto> choices;
}
