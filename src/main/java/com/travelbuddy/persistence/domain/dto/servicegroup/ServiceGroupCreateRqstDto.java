package com.travelbuddy.persistence.domain.dto.servicegroup;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ServiceGroupCreateRqstDto {
    @NotBlank
    private String groupName;
    private List<Integer> siteServiceIds;
}
