package com.travelbuddy.aspectsbytype.admin;
import com.travelbuddy.persistence.domain.dto.aspectsbytype.AspectsByTypeRepresentationRspndDto;
import com.travelbuddy.persistence.domain.entity.AspectsByTypeEntity;

import java.util.List;

public interface AspectsByTypeService {
    Integer addNewAspect(Integer typeId, String aspectName);
    void addNewAspects(Integer typeId, List<String> aspectNames);
    void deleteAspectById(Integer aspectId);
    List<AspectsByTypeEntity> deleteAspectsByIds(List<Integer> aspectIds);
    void deleteAspectByTypeIdAndAspectName(Integer typeId, String aspectName);
    void updateAspectName(Integer aspectId, String aspectName);
    AspectsByTypeRepresentationRspndDto getAspectById(Integer aspectId);
    List<AspectsByTypeRepresentationRspndDto> getAspectsByTypeId(Integer typeId);
}
