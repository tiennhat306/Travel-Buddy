package com.travelbuddy.systemlog.admin;

import com.travelbuddy.common.paging.PageDto;
import com.travelbuddy.persistence.domain.entity.LogEntity;

public interface SystemLogService {
    void log(String level, String message);
    void logBug(String message);
    void logInfo(String message);
    void logWarn(String message);
    PageDto<LogEntity> getLogs(int page);
}
