package com.travelbuddy.report;

import com.travelbuddy.common.exception.errorresponse.NotFoundException;
import com.travelbuddy.persistence.domain.dto.report.ReportCategoryRspnDto;
import com.travelbuddy.persistence.domain.entity.ReportTypeEntity;
import com.travelbuddy.persistence.repository.ReportTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportTypeServiceImpl implements ReportTypeService {
    private final ReportTypeRepository reportTypeRepository;


    @Override
    public List<ReportCategoryRspnDto> getSiteReportCategories(int type) {
        List<ReportTypeEntity> reportTypesDtos = reportTypeRepository.findAllByType(type);
        if (reportTypesDtos.isEmpty()) {
            throw new NotFoundException("Report categories not found");
        }

        return reportTypesDtos.stream()
                .map(reportTypeEntity -> ReportCategoryRspnDto.builder()
                        .id(reportTypeEntity.getCategory().getId())
                        .name(reportTypeEntity.getCategory().getName())
                        .description(reportTypeEntity.getCategory().getDescription())
                        .build())
                .collect(Collectors.toList());
    }
}
