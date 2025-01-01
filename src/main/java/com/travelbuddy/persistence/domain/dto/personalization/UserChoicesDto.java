package com.travelbuddy.persistence.domain.dto.personalization;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class UserChoicesDto {
    private Integer placeId;
    private double rating;

    public UserChoicesDto(Integer placeId) {
        this.placeId = placeId;
        this.rating = 5.0;
    }
}
