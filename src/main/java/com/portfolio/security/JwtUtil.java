package com.portfolio.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;


@Component
public class JwtUtil {

    // 1. FIXED: Set a static secure string (Must be at least 256-bits / 32 characters long)
    private static final String SECRET_STRING = "ShubhamMakodePortfolioSecureStaticKey2026SystemCoreAuthenticationMatrixNode";
    
    // 2. FIXED: Derive a fixed Key object from the stable string bytes
    private final Key key = Keys.hmacShaKeyFor(SECRET_STRING.getBytes());
    
    private final long JWT_EXPIRATION_MS = 1000 * 60 * 60 * 10; // 10 Hours

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key) // Verifies token signatures using our static key
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public String generateToken(String username) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, username);
    }

    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + JWT_EXPIRATION_MS))
                .signWith(key, SignatureAlgorithm.HS256) // Signs cleanly with the stable key
                .compact();
    }

    public Boolean validateToken(String token, String username) {
        final String extractedUsername = extractUsername(token);
        return (extractedUsername.equals(username) && !isTokenExpired(token));
    }
 // Inside com.portfolio.security.JwtUtil.java



 // 🌟 Updated to accept the user's role from your login handler
 public String generateToken(String username, String role) {
     Map<String, Object> claims = new HashMap<>();
     claims.put("role", role); // Inject role (e.g. "ROLE_SUPER_ADMIN") into claims payload
     return createToken(claims, username);
 }
 // 🌟 Extract the roles claim dynamically later

 public String extractRole(String token) {
	    if (token == null) return null;
	    Claims claims = extractAllClaims(token);
	    return claims.get("role", String.class); // Returns null safely if the key doesn't exist
	}
}