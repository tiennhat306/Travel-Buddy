package com.travelbuddy.aspectsbytype.admin;
import com.travelbuddy.common.exception.errorresponse.DataAlreadyExistsException;
import com.travelbuddy.common.exception.errorresponse.NotFoundException;
import com.travelbuddy.persistence.domain.dto.sitetype.SiteTypeRspnDto;
import com.travelbuddy.persistence.domain.entity.AspectsByTypeEntity;
import com.travelbuddy.persistence.domain.entity.SiteTypeEntity;
import com.travelbuddy.persistence.repository.AspectsByTypeRepository;
import com.travelbuddy.persistence.repository.FeeRepository;
import com.travelbuddy.persistence.repository.SiteTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.travelbuddy.persistence.domain.dto.aspectsbytype.*;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AspectsByTypeServiceImp implements AspectsByTypeService {
    private final AspectsByTypeRepository aspectsByTypeRepository;
    private final SiteTypeRepository siteTypeRepository;
    private final FeeRepository feeRepository;

    @Override
    public Integer addNewAspect(Integer typeId, String aspectName) {
        if (aspectsByTypeRepository.existsByTypeIdAndAspectNameIgnoreCase(typeId, aspectName)) {
            throw new DataAlreadyExistsException("Aspect already exists for this type");
        }
        return aspectsByTypeRepository.save(AspectsByTypeEntity.builder().typeId(typeId).aspectName(aspectName).build()).getId();
    }

    @Override
    public void deleteAspectById(Integer aspectId) {
        if (!aspectsByTypeRepository.existsById(aspectId)) {
            throw new NotFoundException("Aspect with id " + aspectId + " not found");
        }
        aspectsByTypeRepository.deleteById(aspectId);
    }

    @Override
    public List<AspectsByTypeEntity> deleteAspectsByIds(List<Integer> aspectIds) {
        List<AspectsByTypeEntity> failed = new ArrayList<AspectsByTypeEntity>();
        for (Integer aspectId : aspectIds) {
            if (feeRepository.existsByAspectId(aspectId)) {
                failed.add(aspectsByTypeRepository.findById(aspectId).get());
            } else deleteAspectById(aspectId);
        }
        return failed;
    }

    @Override
    public void deleteAspectByTypeIdAndAspectName(Integer typeId, String aspectName) {
        if (!aspectsByTypeRepository.existsByTypeIdAndAspectNameIgnoreCase(typeId, aspectName)) {
            throw new NotFoundException("Aspect with type id " + typeId + " and aspect name " + aspectName + " not found");
        }
        aspectsByTypeRepository.deleteByTypeIdAndAspectNameIgnoreCase(typeId, aspectName);
    }

    @Override
    public void updateAspectName(Integer aspectId, String aspectName) {
        if (!aspectsByTypeRepository.existsById(aspectId)) {
            throw new NotFoundException("Aspect with id " + aspectId + " not found");
        }
        AspectsByTypeEntity aspect = aspectsByTypeRepository.findById(aspectId).get();
        aspect.setAspectName(aspectName);
        aspectsByTypeRepository.save(aspect);
    }

    @Override
    public AspectsByTypeRepresentationRspndDto getAspectById(Integer aspectId) {
        AspectsByTypeEntity aspect = aspectsByTypeRepository.findById(aspectId).orElseThrow(() -> new NotFoundException("Aspect with id " + aspectId + " not found"));
        SiteTypeRspnDto siteType = new SiteTypeRspnDto(siteTypeRepository.findById(aspect.getTypeId()).get());
        AspectsByTypeRepresentationRspndDto viewAspect = new AspectsByTypeRepresentationRspndDto();
        viewAspect.setAspectId(aspect.getId());
        viewAspect.setSiteType(siteType);
        viewAspect.setAspectName(aspect.getAspectName());
        return viewAspect;
    }

    @Override
    public List<AspectsByTypeRepresentationRspndDto> getAspectsByTypeId(Integer typeId) {
        List<AspectsByTypeEntity> aspects = aspectsByTypeRepository.findAllByTypeId(typeId).orElseThrow(() -> new NotFoundException("No aspects found for type id " + typeId));
        List<AspectsByTypeRepresentationRspndDto> viewAspects = new ArrayList<AspectsByTypeRepresentationRspndDto>();
        for (AspectsByTypeEntity aspect : aspects) {
            SiteTypeRspnDto siteType = new SiteTypeRspnDto(siteTypeRepository.findById(aspect.getTypeId()).get());
            AspectsByTypeRepresentationRspndDto viewAspectByTypeRspndDto = new AspectsByTypeRepresentationRspndDto();
            viewAspectByTypeRspndDto.setAspectId(aspect.getId());
            viewAspectByTypeRspndDto.setSiteType(siteType);
            viewAspectByTypeRspndDto.setAspectName(aspect.getAspectName());
            viewAspects.add(viewAspectByTypeRspndDto);
        }
        return viewAspects;
    }

    @Override
    public void addNewAspects(Integer typeId, List<String> aspectNames) {
        for (String aspectName : aspectNames) {
            addNewAspect(typeId, aspectName);
        }
    }
}