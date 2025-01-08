package com.travelbuddy.systemlog.admin;

import com.travelbuddy.common.mapper.PageMapper;
import com.travelbuddy.common.paging.PageDto;
import com.travelbuddy.persistence.domain.entity.LogEntity;
import com.travelbuddy.persistence.repository.LogRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

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
    public PageDto<LogEntity> getLogs(int page, String searchText) {
        Pageable pageable = PageRequest.of(page - 1, SYS_LOG_LIMIT);
        Page<LogEntity> logEntities = logRepository.findAllByContentContainingOrLevelContainingOrderByTimestampDesc(searchText, searchText, pageable);
        return pageMapper.toPageDto(logEntities);
    }

    @Override
    public List<LogEntity> getAllLogs() {
        return logRepository.findAll();
    }

    @Override
    public InputStreamResource handleDownloadLogs() {
        List<LogEntity> logs = getAllLogs();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            for (LogEntity log : logs) {
                outputStream.write(log.toString().getBytes(StandardCharsets.UTF_8));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());

        return new InputStreamResource(inputStream);
    }
}
