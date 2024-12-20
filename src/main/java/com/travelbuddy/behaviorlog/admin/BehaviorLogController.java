package com.travelbuddy.behaviorlog.admin;

import com.travelbuddy.persistence.repository.BehaviorLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/behaviors")
@RequiredArgsConstructor
public class BehaviorLogController {
    private final BehaviorLogRepository behaviorLogRepository;

    @GetMapping
    @PreAuthorize("hasAuthority('ACCESS_LOGS')")
    public ResponseEntity<Object> bulkFetch(@RequestParam(name = "user", required = false) Integer userId) {
        // Check if siteVersionId is null
        if (userId == null) {
            // Return all the table
            return ResponseEntity.ok(behaviorLogRepository.findAll());
        }

        return ResponseEntity.ok(behaviorLogRepository.findByUserId(userId));
    }
}
