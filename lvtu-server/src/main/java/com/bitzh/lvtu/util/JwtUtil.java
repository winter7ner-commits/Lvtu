package com.bitzh.lvtu.util;

import io.jsonwebtoken.Claims;
<<<<<<< HEAD
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtil {

    private static final SecretKey SECRET_KEY = Keys.hmacShaKeyFor("lvtu_secret_key_2024_must_be_at_least_32_chars".getBytes());
    private static final long EXPIRATION_TIME = 86400000;

    public static String generateToken(Long userId, String username) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("username", username);
        return Jwts.builder()
                .claims(claims)
                .subject(String.valueOf(userId))
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(SECRET_KEY)
                .compact();
    }

    public static Long getUserId(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(SECRET_KEY)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            return claims.get("userId", Long.class);
=======
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
>>>>>>> 2c7bb808696ce5aba4b1bc1a4e70731964c3986b
        } catch (Exception e) {
            return null;
        }
    }

    public static String getUsername(String token) {
        try {
<<<<<<< HEAD
            Claims claims = Jwts.parser()
                    .verifyWith(SECRET_KEY)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
=======
            Claims claims = parseToken(token);
>>>>>>> 2c7bb808696ce5aba4b1bc1a4e70731964c3986b
            return claims.get("username", String.class);
        } catch (Exception e) {
            return null;
        }
    }
<<<<<<< HEAD

    public static boolean validateToken(String token) {
        try {
            Jwts.parser().verifyWith(SECRET_KEY).build().parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
=======
}
>>>>>>> 2c7bb808696ce5aba4b1bc1a4e70731964c3986b
