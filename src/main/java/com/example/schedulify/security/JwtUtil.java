package com.example.schedulify.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.Map;

@Component
public class JwtUtil {

    // IMPORTANT: replace with env var in production
    private final Key key = Keys.hmacShaKeyFor("VERY_LONG_SECRET_KEY_CHANGE_THIS_TO_ENV_VARIABLE_32_BYTES_MIN".getBytes());
    private final long expirationMs = 1000L * 60 * 60 * 24; // 24 hours

    public String generate(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public Claims parse(String token) throws JwtException {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
    }
}
