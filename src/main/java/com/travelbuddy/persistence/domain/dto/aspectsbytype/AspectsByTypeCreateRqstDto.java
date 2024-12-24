package com.travelbuddy.persistence.domain.dto.aspectsbytype;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class AspectsByTypeCreateRqstDto {
    private Integer typeId;
    private List<String> aspectNames;
}
