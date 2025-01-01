package com.travelbuddy.persistence.domain.dto.personalization;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class CreatePersonalizeRqstDto {
    private Integer userId;
    private List<UserChoicesDto> reviews;

    public CreatePersonalizeRqstDto(Integer userId, List<Integer> choices) {
        this.userId = userId;
        this.reviews = new ArrayList<>();
        for (Integer choice : choices) {
            this.reviews.add(new UserChoicesDto(choice));
        }
    }
}
