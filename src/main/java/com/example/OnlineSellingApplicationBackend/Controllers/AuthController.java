package com.example.OnlineSellingApplicationBackend.Controllers;

import com.example.OnlineSellingApplicationBackend.Security.CustomUserDetails;
import com.example.OnlineSellingApplicationBackend.Security.JwtUtils;
import com.example.OnlineSellingApplicationBackend.Security.UserDetailsServiceImpl;
import com.example.OnlineSellingApplicationBackend.DTO.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final UserDetailsServiceImpl userDetailsService;
    private final JwtUtils jwtUtils;

    public AuthController(AuthenticationManager authenticationManager,
                          UserDetailsServiceImpl userDetailsService,
                          JwtUtils jwtUtils) {
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.jwtUtils = jwtUtils;
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getEmail(),
                            loginRequest.getPassword()
                    )
            );

            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            String token = jwtUtils.generateToken(
                    userDetails.getUsername(),
                    userDetails.getAuthorities().stream()
                            .map(GrantedAuthority::getAuthority)
                            .collect(Collectors.toList()),
                    userDetails.getUserId()
            );

            return ResponseEntity.ok(Map.of(
                    "token", token,
                    "user", Map.of(
                            "id", userDetails.getUserId(),
                            "email", userDetails.getUsername(),
                            "roles", userDetails.getAuthorities()
                    )
            ));

        } catch (BadCredentialsException e) {
            return ResponseEntity.status(401).body(Map.of(
                    "message", "Invalid email or password",
                    "status", 401
            ));
        } catch (AuthenticationException e) {
            return ResponseEntity.status(401).body(Map.of(
                    "message", "Authentication failed",
                    "status", 401
            ));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                    "message", "Internal server error",
                    "status", 500
            ));
        }
    }
}
