package com.app.focus.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.function.Function;

@Service
@Slf4j
public class JwtService {

    @Value("${jwt.secret}")
    private String SECRET_KEY;

    @Value("${jwt.expiration-ms:86400000}") // 24 hours by default
    private long EXPIRATION_MS;

    private Key getSigningKey() {
        byte[] keyBytes = Base64.getDecoder().decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateToken(UserDetails userDetails) {
        String username = userDetails.getUsername();
        log.debug("Generating JWT token for user: {}", username);

        try {
            String token = Jwts.builder()
                    .setSubject(username)
                    .claim("roles", userDetails.getAuthorities().stream()
                            .map(Object::toString)
                            .toList())
                    .setIssuedAt(new Date())
                    .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_MS))
                    .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                    .compact();

            log.info("JWT token successfully generated for user: {}", username);
            return token;
        } catch (Exception e) {
            log.error("Failed to generate token for user {}: {}", username, e.getMessage(), e);
            throw e;
        }
    }

    public String extractUsername(String token) {
        try {
            String username = extractClaim(token, Claims::getSubject);
            log.debug("Extracted username from token: {}", username);
            return username;
        } catch (Exception e) {
            log.warn("Unable to extract username from token: {}", e.getMessage());
            return null;
        }
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            log.info("JWT has expired: {}", e.getMessage());
            throw e;
        } catch (JwtException e) {
            log.warn("JWT parsing failed: {}", e.getMessage());
            throw e;
        }
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private boolean isTokenExpired(String token) {
        boolean expired = extractExpiration(token).before(new Date());
        if (expired) {
            log.info("JWT token is expired");
        } else {
            log.debug("JWT token is not expired");
        }
        return expired;
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        boolean isValid = username != null &&
                username.equals(userDetails.getUsername()) &&
                !isTokenExpired(token);

        if (isValid) {
            log.info("Token is valid for user: {}", username);
        } else {
            log.warn("Token is invalid for user: {}", userDetails.getUsername());
        }

        return isValid;
    }
}
