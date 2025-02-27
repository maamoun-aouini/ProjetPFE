package com.example.OnlineSellingApplicationBackend.WebMvcConfigurer;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(request -> null)) // Configure CORS if needed
                .csrf(csrf -> csrf.disable()) // Disable CSRF (use cautiously in production)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/**").permitAll() // Allow access to /api
                        .anyRequest().authenticated() // Require authentication for other endpoints
                );

        return http.build();
    }
}
