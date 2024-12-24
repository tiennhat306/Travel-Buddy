package com.travelbuddy.systemlog.admin;

import com.travelbuddy.persistence.repository.BehaviorLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
    public ResponseEntity<Object> getSystemLogs(@RequestParam(name = "page", required = false, defaultValue = "1") int page) {
        return ResponseEntity.ok(systemLogService.getLogs(page));
    }
}
