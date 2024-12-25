package com.travelbuddy.auth.controller;

import com.travelbuddy.auth.service.TokenStoreService;
import com.travelbuddy.auth.service.UserAuthService;
import com.travelbuddy.common.exception.errorresponse.DataAlreadyExistsException;
import com.travelbuddy.common.exception.errorresponse.InvaidTokenException;
import com.travelbuddy.persistence.domain.dto.auth.*;
import com.travelbuddy.persistence.domain.entity.TokenStoreEntity;
import com.travelbuddy.systemlog.admin.SystemLogService;
import com.travelbuddy.user.UserService;
import jakarta.validation.Valid;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class UserAuthController {
    private final UserAuthService userAuthService;
    private final SystemLogService systemLogService;
    private final UserService userService;

    private final TokenStoreService tokenStoreService;

    @PostMapping("/login")
    public ResponseEntity<Object> postLogin(@RequestBody @Valid LoginRqstDto loginRqstDto) {
        LoginRspnDto loginRspnDto = userAuthService.login(loginRqstDto);
        return ResponseEntity.ok(loginRspnDto);
    }

    @PostMapping("/register")
    public ResponseEntity<Object> postRegister(@RequestBody @Valid RegisterRqstDto registerRqstDto) {
        if(userService.isUserExists(registerRqstDto.getEmail())) {
            throw new DataAlreadyExistsException("Email already exists");
        }

        userAuthService.register(registerRqstDto);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/confirm-registration")
    public ResponseEntity<Void> confirmRegistration(@RequestBody @Valid VerificationOtpRqstDto verificationOtpRqstToken) {
        userAuthService.confirmRegistration(verificationOtpRqstToken);
        systemLogService.logInfo("New user registered");
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/reset-password")
    public ResponseEntity<Void> resetPassword(@RequestBody @Valid ResetPasswordRqstDto resetPasswordRqstDto) {
        userAuthService.resetPassword(resetPasswordRqstDto);
        systemLogService.logInfo(resetPasswordRqstDto.getEmail() + " requested to reset password");
        return ResponseEntity.accepted().build();
    }

    @PostMapping("/validate-reset-password")
    public ResponseEntity<Object> validateResetPassword(@RequestBody @Valid VerificationOtpRqstDto verificationOtpRqstDto) {
        userAuthService.validateResetPassword(verificationOtpRqstDto);
        systemLogService.logInfo(verificationOtpRqstDto.getEmail() + " validated reset password");
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/reset-password")
    public ResponseEntity<Void> resetPassword(@RequestBody @Valid ConfirmNewPasswordRqstDto confirmNewPasswordRqstDto) {
        userAuthService.confirmNewPassword(confirmNewPasswordRqstDto);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestHeader("Authorization") String token) {
        tokenStoreService.deleteToken(token);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<Object> refreshToken(@RequestHeader("Authorization") String token) {
        if (token == null || !token.startsWith("Bearer ")) {
            throw new InvaidTokenException("Refresh token is required");
        }

        Optional<TokenStoreEntity> tokenStoreEntity = tokenStoreService.findByToken(token.substring(7));

        if(tokenStoreEntity.isEmpty()) {
            throw new InvaidTokenException("Invalid refresh token");
        }

        String accessToken = userAuthService.refreshToken(tokenStoreEntity.get().getUserId());

        return ResponseEntity.ok(TokenRspnDto.builder()
                .accessToken(accessToken)
                .tokenType("Bearer")
                .build());
    }
}
