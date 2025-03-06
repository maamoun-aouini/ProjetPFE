package com.example.OnlineSellingApplicationBackend.Security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;

@Component
public class JwtUtils {
    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);  // Add this line

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private int expiration;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    public String generateToken(String username, List<String> roles, Long userId) {
        return Jwts.builder()
                .setSubject(username)
                .claim("roles", roles)
                .claim("userId", userId)
                .setIssuer("your-issuer") // Fixed issuer
                .setAudience("your-audience") // Intended audience
                .setId(UUID.randomUUID().toString()) // Unique JWT ID
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey())
                .compact();
    }
    public Claims extractClaims(String token) {
        return Jwts.parser()
                .setSigningKey(getSigningKey())
                .parseClaimsJws(token)
                .getBody();
    }
    public String getUsernameFromToken(String token) {
        return extractClaims(token).getSubject();
    }
    // JwtUtils.java
    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(getSigningKey()).parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException ex) {
            logger.error("Token expired: {}", ex.getMessage());
        } catch (JwtException | IllegalArgumentException ex) {
            logger.error("Invalid token: {}", ex.getMessage());
        }
        return false;
    }
}