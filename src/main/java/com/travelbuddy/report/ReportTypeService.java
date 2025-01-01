package com.travelbuddy.report;

import com.travelbuddy.persistence.domain.dto.report.ReportCategoryRspnDto;

import java.util.List;

public interface ReportTypeService {
    List<ReportCategoryRspnDto> getSiteReportCategories(int type);
}
