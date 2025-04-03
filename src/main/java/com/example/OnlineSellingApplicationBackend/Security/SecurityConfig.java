package com.example.OnlineSellingApplicationBackend.Security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
    private final JwtAuthEntryPoint authEntryPoint;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthEntryPoint authEntryPoint,
                          JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.authEntryPoint = authEntryPoint;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource())) // Enable CORS
                .exceptionHandling(eh -> eh.authenticationEntryPoint(authEntryPoint))
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/clients/all").hasAnyAuthority("ROLE_ADMIN", "ROLE_SUPERADMIN") //hasAnyAuthority("ROLE_ADMIN", "ROLE_SUPERADMIN")
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/uploads/**").permitAll()
                        .requestMatchers("/uploads/**").permitAll()
                        .requestMatchers( "/api/test/ping").permitAll()
                        .requestMatchers("/api/clients/register").permitAll()

                        // Client endpoints
                        .requestMatchers("/api/clients/stats/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_SUPERADMIN")
                        .requestMatchers("/api/clients/{clientId}").hasAnyAuthority("ROLE_ADMIN", "ROLE_SUPERADMIN")// All other client endpoints require authentication

                        .requestMatchers("/api/clients/products").hasAnyAuthority("ROLE_USERSTANDARD", "ROLE_USERPARTNER")
                        .requestMatchers("/api/clients/categories/**").hasAnyAuthority("ROLE_USERSTANDARD", "ROLE_USERPARTNER", "ROLE_ADMIN", "ROLE_SUPERADMIN")
                        .requestMatchers("/api/clients/{clientId}/favorites/**").hasAnyAuthority("ROLE_USERSTANDARD", "ROLE_USERPARTNER")
                        .requestMatchers("/api/clients/{clientId}/ratings/**").hasAnyAuthority("ROLE_USERSTANDARD", "ROLE_USERPARTNER")
                        .requestMatchers("/api/clients/{clientId}/password").hasAnyAuthority("ROLE_USERSTANDARD", "ROLE_USERPARTNER", "ROLE_ADMIN", "ROLE_SUPERADMIN")
                        .requestMatchers("/api/clients/{clientId}").hasAnyAuthority("ROLE_ADMIN", "ROLE_SUPERADMIN")// All other client endpoints require authentication
                        .requestMatchers("/api/clients/**").authenticated() // All other client endpoints require authentication

                        // Admin endpoints

                        .requestMatchers("/api/admin/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_SUPERADMIN")
                        .requestMatchers("/api/admin/profile").hasAnyAuthority("ROLE_ADMIN")
                        .requestMatchers("/api/packs/**").hasAnyAuthority("ROLE_USERPARTNER", "ROLE_ADMIN", "ROLE_SUPERADMIN")

                        // SuperAdmin endpoints
                        .requestMatchers( "/api/superadmin/dashboard-stats").hasAnyAuthority("ROLE_ADMIN", "ROLE_SUPERADMIN")
                        .requestMatchers("/api/superadmin/**").hasAuthority("ROLE_SUPERADMIN")
                        .requestMatchers( "/api/superadmin/admins/**").hasAuthority("ROLE_SUPERADMIN")                        //.requestMatchers("/api/superadmin/**").hasAuthority("ROLE_SUPERADMIN")

                        // Product endpoints
                        .requestMatchers("/api/Products/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_SUPERADMIN")

                        // Commande endpoints
                        // .requestMatchers("/api/commandes/orders/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_SUPERADMIN")
                        //.requestMatchers("/api/commandes/{orderId}/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_SUPERADMIN")
                        //  .requestMatchers("/api/commandes/{clientId}/Pack").hasAuthority("ROLE_USERPARTNER")
                        //.requestMatchers("/api/commandes/{clientId}").hasAnyAuthority("ROLE_USERSTANDARD", "ROLE_USERPARTNER")
                        .requestMatchers("/api/commandes/**").permitAll()

                        // Categories endpoints
                        .requestMatchers("/api/categories/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_SUPERADMIN")

                        // Reclamation endpoints
                        .requestMatchers("/api/reclamations/clients/**").hasAnyAuthority("ROLE_USERSTANDARD", "ROLE_USERPARTNER", "ROLE_ADMIN", "ROLE_SUPERADMIN")
                        .requestMatchers("/api/reclamations").hasAnyAuthority("ROLE_ADMIN", "ROLE_SUPERADMIN")
                        .requestMatchers("/api/reclamations/**").authenticated()

                        // Fallback: All other requests require authentication
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList(
                "http://localhost:8081",
                "http://192.168.1.111:8081",
                "http://10.0.2.2:8081",
                "exp://192.168.1.111:19000" // Expo default
        ));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setExposedHeaders(Arrays.asList("Authorization", "content-type"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }


}