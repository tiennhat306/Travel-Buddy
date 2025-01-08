package com.travelbuddy.systemlog.admin;

import com.travelbuddy.persistence.domain.entity.LogEntity;
import com.travelbuddy.persistence.repository.BehaviorLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequestMapping("/api/admin/logs")
@RequiredArgsConstructor
public class LogController {
    private final BehaviorLogRepository behaviorLogRepository;
    private final SystemLogService systemLogService;

    @GetMapping("/users")
    @PreAuthorize("hasAuthority('ACCESS_LOGS')")
    public ResponseEntity<Object> getAllUserBehaviorLogs(@RequestParam(name = "user", required = false) Integer userId) {
        // Check if siteVersionId is null
        if (userId == null) {
            // Return all the table
            return ResponseEntity.ok(behaviorLogRepository.findAll());
        }
        return ResponseEntity.ok(behaviorLogRepository.findByUserId(userId));
    }

    @GetMapping("/sys")
    @PreAuthorize("hasAuthority('ACCESS_LOGS')")
    public ResponseEntity<Object> getSystemLogs(@RequestParam(name = "page", required = false, defaultValue = "1") int page,
                                                @RequestParam(name = "searchText", required = false) String searchText) {
        return ResponseEntity.ok(systemLogService.getLogs(page, searchText));
    }

    @GetMapping("/download-logs")
    @PreAuthorize("hasAuthority('ACCESS_LOGS')")
    public ResponseEntity<InputStreamResource> downloadSystemLogs() throws IOException {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=system_logs.txt");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.TEXT_PLAIN)
                .body(systemLogService.handleDownloadLogs());
    }
}
