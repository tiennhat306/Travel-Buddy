package com.travelbuddy.personalization.user;

import com.travelbuddy.common.utils.RequestUtils;
import com.travelbuddy.persistence.domain.dto.personalization.PersonalizeSubmitRqstDto;
import com.travelbuddy.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/personalization")
@RequiredArgsConstructor
public class PersonalizationController {
    private final PersonalizationService personalizationService;
    private final UserService userService;
    private final RequestUtils requestUtils;

    @GetMapping()
    public ResponseEntity<Object> getPersonalization() {
        return ResponseEntity.ok(personalizationService.getChoices());
    }

    @PostMapping()
    public ResponseEntity<Object> submitChoices(@RequestBody PersonalizeSubmitRqstDto personalizeSubmitRqstDto) {
        return ResponseEntity.ok(personalizationService.submitChoices(requestUtils.getUserIdCurrentRequest(), personalizeSubmitRqstDto.getSelectedIds()));
    }

    @GetMapping("/recommendations")
    public ResponseEntity<Object> getRecommendations() {
        return ResponseEntity.ok(personalizationService.getRecommendations(requestUtils.getUserIdCurrentRequest()));
    }
}
