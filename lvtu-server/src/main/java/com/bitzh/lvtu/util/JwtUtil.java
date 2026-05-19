package com.bitzh.lvtu.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.util.Date;

public class JwtUtil {

    private static final String SECRET = "Lvtu2026AuthSecretKeyMustBeLongEnoughForHmacSha256";
    private static final long EXPIRATION_MILLIS = 24 * 60 * 60 * 1000L;
    private static final Key KEY = Keys.hmacShaKeyFor(SECRET.getBytes());

    public static String generateToken(Long userId, String username) {
        long now = System.currentTimeMillis();
        return Jwts.builder()
                .setSubject(String.valueOf(userId))
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(now + EXPIRATION_MILLIS))
                .claim("username", username)
                .signWith(KEY, SignatureAlgorithm.HS256)
                .compact();
    }

    public static Claims parseToken(String token) {
        Jws<Claims> jws = Jwts.parserBuilder()
                .setSigningKey(KEY)
                .build()
                .parseClaimsJws(token);
        return jws.getBody();
    }

    public static Long getUserId(String token) {
        try {
            Claims claims = parseToken(token);
            String subject = claims.getSubject();
            if (subject == null || subject.isEmpty()) {
                return null;
            }
            return Long.valueOf(subject);
        } catch (Exception e) {
            return null;
        }
    }

    public static String getUsername(String token) {
        try {
            Claims claims = parseToken(token);
            return claims.get("username", String.class);
        } catch (Exception e) {
            return null;
        }
    }
}
