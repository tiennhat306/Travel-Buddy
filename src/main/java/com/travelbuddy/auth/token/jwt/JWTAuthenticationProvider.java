package com.travelbuddy.auth.token.jwt;

import com.travelbuddy.auth.token.BearerAuthenticationToken;
import com.travelbuddy.auth.token.InvalidBearerTokenException;
import com.travelbuddy.persistence.repository.AdminRepository;
import com.travelbuddy.persistence.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class JWTAuthenticationProvider implements AuthenticationProvider {
    private final JWTProcessor jwtProcessor;
    private final UserRepository userRepository;
    private final AdminRepository adminRepository;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        try {
            String token = (String) authentication.getCredentials();
            VerifiedJWT verifiedJWT = jwtProcessor.getVerifiedJWT(token);
            String email = verifiedJWT.getUsername();

            boolean isValidUser = false;
            if (userRepository.existsAndEnabledByEmail(email)) {
                isValidUser = true;
            } else if (adminRepository.existsAndEnabledByEmail(email)) {
                isValidUser = true;
            }
            if (!isValidUser) {
                throw new InvalidJWTException("Invalid user");
            }

            List<GrantedAuthority> authorities = getAuthorities(verifiedJWT);

            return new JWTAuthenticationToken(verifiedJWT, authorities);
        }catch (InvalidJWTException e) {
            throw new InvalidBearerTokenException(e.getMessage());
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return BearerAuthenticationToken.class.isAssignableFrom(authentication);
    }

    private List<GrantedAuthority> getAuthorities(VerifiedJWT verifiedJWT) {
        List<String> authoritiesClaim = verifiedJWT.getListClaim("scp", String.class)
                .orElse(new ArrayList<>());

        List<GrantedAuthority> authorities = new ArrayList<>();
        authoritiesClaim.forEach(authority ->
                authorities.add(new SimpleGrantedAuthority(authority)));

        return authorities;
    }
}
