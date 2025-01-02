package com.travelbuddy.personalization.user;

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

    @GetMapping()
    public ResponseEntity<Object> getPersonalization() {
        return ResponseEntity.ok(personalizationService.getChoices());
    }

    @PostMapping()
    public ResponseEntity<Object> submitChoices(@RequestBody PersonalizeSubmitRqstDto personalizeSubmitRqstDto) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Integer userId = userService.getUserIdByEmailOrUsername(username);
        Object res = personalizationService.submitChoices(userId, personalizeSubmitRqstDto.getSelectedIds());
        return ResponseEntity.ok(res);
    }
}
