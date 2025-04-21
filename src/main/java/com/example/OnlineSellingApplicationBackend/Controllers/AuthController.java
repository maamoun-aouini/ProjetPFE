package com.example.OnlineSellingApplicationBackend.Controllers;

import com.example.OnlineSellingApplicationBackend.Security.CustomUserDetails;
import com.example.OnlineSellingApplicationBackend.Security.JwtUtils;
import com.example.OnlineSellingApplicationBackend.Security.UserDetailsServiceImpl;
import com.example.OnlineSellingApplicationBackend.Services.EmailService;
import com.example.OnlineSellingApplicationBackend.Services.OtpService;
import com.example.OnlineSellingApplicationBackend.Services.ClientService;
import com.example.OnlineSellingApplicationBackend.DTO.*;
import com.example.OnlineSellingApplicationBackend.entities.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final UserDetailsServiceImpl userDetailsService;
    private final JwtUtils jwtUtils;
    private final ClientService clientService;
    private final EmailService emailService;
    private final OtpService otpService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AuthController(AuthenticationManager authenticationManager,
                          UserDetailsServiceImpl userDetailsService,
                          JwtUtils jwtUtils,
                          ClientService clientService,
                          EmailService emailService,
                          OtpService otpService,
                          PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.jwtUtils = jwtUtils;
        this.clientService = clientService;
        this.emailService = emailService;
        this.otpService = otpService;
        this.passwordEncoder = passwordEncoder;
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
            // Update last login time
            clientService.updateLastLogin(userDetails.getUserId(), LocalDateTime.now());

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

    @PostMapping("/google-login")
    public ResponseEntity<?> handleGoogleLogin(@RequestBody GoogleAuthRequest googleAuthRequest) {
        try {
            // Verify the Google ID token with Google's API
            GoogleUserInfo googleUserInfo = verifyGoogleIdToken(googleAuthRequest.getIdToken());

            if (googleUserInfo == null) {
                return ResponseEntity.status(401).body(Map.of(
                        "message", "Invalid Google token",
                        "status", 401
                ));
            }

            // Check if user exists with this email
            Optional<Client> existingClient = clientService.findClientByEmail(googleUserInfo.getEmail());

            if (existingClient.isPresent()) {
                Client client = existingClient.get();

                // Check if email is verified
                if (!client.isActif()) {
                    // Send verification email and return 202 Accepted
                    String verificationCode = otpService.generateOtp(client.getEmail());
                    emailService.sendVerificationEmail(client.getEmail(), verificationCode);

                    return ResponseEntity.status(202).body(Map.of(
                            "message", "Email verification required",
                            "email", client.getEmail(),
                            "status", 202
                    ));
                }

                // Update last login time
                clientService.updateLastLogin(client.getId(), LocalDateTime.now());

                // Generate JWT
                CustomUserDetails userDetails = (CustomUserDetails) userDetailsService.loadUserByUsername(client.getEmail());
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
                                "id", client.getId(),
                                "email", client.getEmail(),
                                "roles", userDetails.getAuthorities()
                        )
                ));
            } else {
                // Create a new client with info from Google
                String randomPassword = UUID.randomUUID().toString();

                Client newClient = new Client();
                newClient.setNom(googleUserInfo.getName());
                newClient.setEmail(googleUserInfo.getEmail());
                newClient.setMotDePasse(passwordEncoder.encode(randomPassword));
                newClient.setProfil(googleUserInfo.getPictureUrl());
                newClient.setType(TypeClient.Individual);
                newClient.setActif(false); // Require email verification

                Client savedClient = clientService.registerClientWithGoogle(newClient);

                // Send verification email
                String verificationCode = otpService.generateOtp(savedClient.getEmail());
                emailService.sendVerificationEmail(savedClient.getEmail(), verificationCode);

                return ResponseEntity.status(202).body(Map.of(
                        "message", "Email verification required for new account",
                        "email", savedClient.getEmail(),
                        "status", 202
                ));
            }

        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                    "message", "Google authentication failed: " + e.getMessage(),
                    "status", 500
            ));
        }
    }

    @PostMapping("/verify-email")
    public ResponseEntity<?> verifyEmail(@RequestParam String email, @RequestParam String code) {
        try {
            if (otpService.validateOtp(email, code)) {
                // Activate the client account
                Client client = clientService.activateClientAccount(email);

                // Generate JWT
                CustomUserDetails userDetails = (CustomUserDetails) userDetailsService.loadUserByUsername(client.getEmail());
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
                                "id", client.getId(),
                                "email", client.getEmail(),
                                "roles", userDetails.getAuthorities()
                        )
                ));
            } else {
                return ResponseEntity.status(401).body(Map.of(
                        "message", "Invalid verification code",
                        "status", 401
                ));
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                    "message", "Verification failed: " + e.getMessage(),
                    "status", 500
            ));
        }
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> initiatePasswordReset(@RequestParam String email) {
        try {
            if (!clientService.emailExists(email)) {
                return ResponseEntity.ok().build(); // Don't reveal if email exists
            }

            String otp = otpService.generateOtp(email);
            emailService.sendPasswordResetOtp(email, otp);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error processing request");
        }
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOtp(@RequestParam String email, @RequestParam String otp) {
        try {
            if (otpService.validateOtp(email, otp)) {
                return ResponseEntity.ok().build();
            }
            return ResponseEntity.status(401).body("Invalid OTP");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error verifying OTP");
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestParam String email,
                                           @RequestParam String otp,
                                           @RequestParam String newPassword) {
        try {
            if (!otpService.validateOtp(email, otp)) {
                return ResponseEntity.status(401).body("Invalid OTP");
            }

            if (newPassword.length() < 8) {
                return ResponseEntity.badRequest().body("Password must be at least 8 characters");
            }

            Client client = clientService.resetPasswordByEmail(email, newPassword);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error resetting password");
        }
    }

    // Helper method to verify Google ID token
    private GoogleUserInfo verifyGoogleIdToken(String idToken) {
        // In a real implementation, you would use Google's API client library to verify the token
        // For this example, we'll assume the token is valid and extract info from it

        // This is a placeholder for demonstration purposes
        // In production, use Google's API to verify the token
        try {
            // Simulated verification
            // In a real application, you would use Google's API to verify the token
            // and get the user information

            // For demonstration, we'll extract a fake user from the token
            // In production, you must properly verify with Google API

            // Sample implementation:
            // HttpClient client = HttpClients.createDefault();
            // HttpGet request = new HttpGet("https://oauth2.googleapis.com/tokeninfo?id_token=" + idToken);
            // HttpResponse response = client.execute(request);
            // String jsonResponse = EntityUtils.toString(response.getEntity());
            // ObjectMapper mapper = new ObjectMapper();
            // JsonNode node = mapper.readTree(jsonResponse);

            // Here we're simulating that part
            return new GoogleUserInfo(
                    "google_" + Math.abs(idToken.hashCode()), // Simulated sub
                    "user_" + idToken.substring(0, 5) + "@gmail.com", // Simulated email
                    "Google User " + idToken.substring(0, 5), // Simulated name
                    "https://ui-avatars.com/api/?name=Google+User&background=random" // Simulated picture
            );
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Helper class to store Google user info
    private static class GoogleUserInfo {
        private final String sub;
        private final String email;
        private final String name;
        private final String pictureUrl;

        public GoogleUserInfo(String sub, String email, String name, String pictureUrl) {
            this.sub = sub;
            this.email = email;
            this.name = name;
            this.pictureUrl = pictureUrl;
        }

        public String getSub() {
            return sub;
        }

        public String getEmail() {
            return email;
        }

        public String getName() {
            return name;
        }

        public String getPictureUrl() {
            return pictureUrl;
        }
    }

    // DTO class for Google auth request
    private static class GoogleAuthRequest {
        private String idToken;

        public String getIdToken() {
            return idToken;
        }

        public void setIdToken(String idToken) {
            this.idToken = idToken;
        }
    }
}