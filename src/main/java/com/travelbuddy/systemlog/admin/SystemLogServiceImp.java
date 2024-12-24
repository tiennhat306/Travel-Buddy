package com.travelbuddy.systemlog.admin;

import com.travelbuddy.common.mapper.PageMapper;
import com.travelbuddy.common.paging.PageDto;
import com.travelbuddy.persistence.domain.entity.LogEntity;
import com.travelbuddy.persistence.repository.LogRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import static com.travelbuddy.common.constants.PaginationLimitConstants.SYS_LOG_LIMIT;

@Service
@Transactional
@RequiredArgsConstructor
public class SystemLogServiceImp implements SystemLogService {
    private final LogRepository logRepository;
    private final PageMapper pageMapper;

    @Override
    public void log(String level, String message) {
        LogEntity logEntity = LogEntity.builder()
                .level(level)
                .content(message)
                .timestamp(new java.sql.Timestamp(System.currentTimeMillis()))
                .build();
        logRepository.save(logEntity);
    }

    @Override
    public void logBug(String message) {
        log("BUG", message);
    }

    @Override
    public void logInfo(String message) {
        log("INFO", message);
    }

    @Override
    public void logWarn(String message) {
        log("WARN", message);
    }

    @Override
    public PageDto<LogEntity> getLogs(int page) {
        Pageable pageable = PageRequest.of(page - 1, SYS_LOG_LIMIT);
        Page<LogEntity> logEntities = logRepository.findAllByOrderByTimestampDesc(pageable);
        return pageMapper.toPageDto(logEntities);
    }
}
