package com.travelbuddy.persistence.domain.dto.account.admin;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class GenericAssociationRqstDto {
    public Integer parentId;
    public List<Integer> dependencyIds;
}
