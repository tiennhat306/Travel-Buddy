package com.travelbuddy.config;

import com.travelbuddy.auth.filter.BearerTokenAuthenticationFilter;
import com.travelbuddy.auth.token.jwt.JWTAuthenticationProvider;
import com.travelbuddy.auth.token.jwt.JWTProcessor;
import com.travelbuddy.persistence.repository.AdminRepository;
import com.travelbuddy.persistence.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.Admin;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.context.SecurityContextHolderFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class WebSecurityConfig {
    private final UserRepository userRepository;
    private final AdminRepository adminRepository;

    @Bean
    @Order(1)
    public SecurityFilterChain webSocketSecurityFilterChain(HttpSecurity http,
                                                            JWTProcessor jwtProcessor) throws Exception {
        AuthenticationManager authenticationManager = authManager(http, jwtProcessor);

        http.csrf(AbstractHttpConfigurer::disable);
        http.authenticationManager(authenticationManager)
                .sessionManagement((sessionManagement) -> sessionManagement
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterAfter(new BearerTokenAuthenticationFilter(authenticationManager), SecurityContextHolderFilter.class);

        http.securityMatcher("/ws/**")
                .authorizeHttpRequests(authorize -> authorize
                        .anyRequest().permitAll()
                ).cors(cors -> cors.configurationSource(corsConfigurationSource()));

        return http.build();
    }

    @Bean
    @Order(2)
    public SecurityFilterChain adminSecurityFilterChain(HttpSecurity http,
                                                        JWTProcessor jwtProcessor) throws Exception {
        AuthenticationManager authenticationManager = authManager(http, jwtProcessor);

        http.csrf(AbstractHttpConfigurer::disable);
        http.authenticationManager(authenticationManager)
                .sessionManagement((sessionManagement) -> sessionManagement
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterAfter(new BearerTokenAuthenticationFilter(authenticationManager), SecurityContextHolderFilter.class);

        http.securityMatcher("/api/admin/**")
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/api/admin/auth/**").permitAll()
                        .anyRequest().authenticated()
                ).cors(cors -> cors.configurationSource(corsConfigurationSource()));

        return http.build();
    }

    @Bean
    @Order(3)
    public SecurityFilterChain userSecurityFilterChain(HttpSecurity http,
                                                       JWTProcessor jwtProcessor) throws Exception {
        AuthenticationManager authenticationManager = authManager(http, jwtProcessor);

        http.csrf(AbstractHttpConfigurer::disable);
        http.authenticationManager(authenticationManager)
                .sessionManagement((sessionManagement) -> sessionManagement
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterAfter(new BearerTokenAuthenticationFilter(authenticationManager), SecurityContextHolderFilter.class);

        http.securityMatcher("/api/**")
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers(
                                "/api/sites/*",
                                "/api/sites/search",
                                "/api/sites/discover",
                                "/api/sites/@",
                                "/api/sites/*/reviews" // Đảm bảo các review không cần auth
                        ).permitAll()
                        .requestMatchers("/api/sites/my-sites").authenticated()
                        .anyRequest().authenticated()
                ).cors(cors -> cors.configurationSource(corsConfigurationSource()));

        return http.build();
    }

    @Bean
    public PasswordEncoder getPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    private AuthenticationManager authManager(HttpSecurity http, JWTProcessor jwtProcessor) throws Exception {
        return http.getSharedObject(AuthenticationManagerBuilder.class)
                .authenticationProvider(jwtAuthProvider(jwtProcessor))
                .build();
    }

    private AuthenticationProvider jwtAuthProvider(JWTProcessor jwtProcessor) {
        return new JWTAuthenticationProvider(jwtProcessor, userRepository, adminRepository);
    }

    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
