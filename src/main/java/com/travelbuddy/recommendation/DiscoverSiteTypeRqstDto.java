package com.travelbuddy.recommendation;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class DiscoverSiteTypeRqstDto {
    private List<Integer> typeIds;
}
