package com.example.utils;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;

@Service
public class JwtService {

    // claims : register, (custom) private, public
    // signature
    // openssl rand -hex 32
    @Value("${jwt.secret}")
    private String SECRET_KEY;

    public void validateToken(final String token) {
        Jwts.parserBuilder().setSigningKey(getSignInKey()).build().parseClaimsJws(token);
    }

    // Key
    private Key getSignInKey() {
        System.out.println("-----------------------------------------------------");
        System.out.println("secret key : "+ SECRET_KEY);
        System.out.println("-----------------------------------------------------");

        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
