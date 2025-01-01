package com.travelbuddy.homepage.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/homepage")
@RequiredArgsConstructor
public class HomepageController {
    private final HomepageService homepageService;
    @GetMapping()
    public ResponseEntity<Object> getHomepage() {
        return ResponseEntity.ok(homepageService.getHomepageOverview());
    }
}
