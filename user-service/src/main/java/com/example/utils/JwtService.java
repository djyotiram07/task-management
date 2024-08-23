package com.example.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.*;
import java.util.function.Function;

@Service
public class JwtService {

    // claims : register, (custom) private, public
    // signature
    // openssl rand -hex 32
    @Value("${jwt.secret}")
    private String SECRET_KEY;

    public Set<String> extractRoles(String jwtToken) {
        Claims claims = extractAllClaims(jwtToken.substring(7));

        List<String> roles = Optional.ofNullable(claims.get("roles", List.class))
                .map(list -> (List<String>) list)
                .orElseGet(List::of);

        return new HashSet<>(roles);
    }

    // Claims
    private Claims extractAllClaims(String token) {

        return Jwts.parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimResolver) {

        Claims claims = extractAllClaims(token);
        return claimResolver.apply(claims);
    }

    // Key
    private Key getSignInKey() {

        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}

