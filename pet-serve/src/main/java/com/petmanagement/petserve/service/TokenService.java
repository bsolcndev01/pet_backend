package com.petmanagement.petserve.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class TokenService {
    private final Map<String, Integer> tokenUserMap = new ConcurrentHashMap<>();
    private final Set<String> revokedJtis = ConcurrentHashMap.newKeySet();

    @Value("${security.jwt.secret}")
    private String secret;

    @Value("${security.jwt.expiration-hours:168}")
    private long expirationHours;

    private SecretKey signingKey;

    @PostConstruct
    public void init() {
        // HS256 需要至少 256 位的 key
        this.signingKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public String issue(Integer userId) {
        String jti = UUID.randomUUID().toString();
        Instant now = Instant.now();
        Instant expiry = now.plus(Duration.ofHours(expirationHours));

        String token = Jwts.builder()
                .setId(jti)
                .setSubject(String.valueOf(userId))
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(expiry))
                .signWith(signingKey, SignatureAlgorithm.HS256)
                .compact();
        // 兼容老接口：在内存里记一份，便于后续从 JWT 的 subject 反查用户
        tokenUserMap.put(token, userId);
        return token;
    }

    public Optional<Integer> resolve(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(signingKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            if (revokedJtis.contains(claims.getId())) {
                return Optional.empty();
            }
            String subject = claims.getSubject();
            if (subject == null) {
                return Optional.empty();
            }
            return Optional.of(Integer.parseInt(subject));
        } catch (ExpiredJwtException e) {
            return Optional.empty();
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public void revoke(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(signingKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            revokedJtis.add(claims.getId());
        } catch (Exception ignored) {
            // ignore invalid token
        }
        tokenUserMap.remove(token);
    }
}
