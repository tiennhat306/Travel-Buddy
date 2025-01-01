package com.travelbuddy.persistence.domain.dto.personalization;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class AssociationSiteTypeAndSiteDto {
    private Integer siteTypeId;
    private Integer siteId;
    private Integer siteVersionId;
    private String siteName;
    private String pictureUrl;
}
