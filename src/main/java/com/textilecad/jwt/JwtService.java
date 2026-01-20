package com.textilecad.jwt;

import java.util.Date;
import java.util.function.Function;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {
  private final SecretKey KEY;
  private final Long jwtTokenLifetime;

  public JwtService(
      @Value("${security.jwt.secret-key}") String jwtSecret,
      @Value("${security.jwt.expiration-time}") Long jwtTokenLifetime
  ) {
    this.KEY = Keys.hmacShaKeyFor(jwtSecret.getBytes());
    this.jwtTokenLifetime = jwtTokenLifetime;
  }

  public String generateToken(UserDetails userDetails) {
    return Jwts.builder()
      .subject(userDetails.getUsername())
      .issuedAt(new Date(System.currentTimeMillis()))
      .expiration(new Date(System.currentTimeMillis() + jwtTokenLifetime))
      .signWith(KEY)
      .compact();
  }

  public String extractUsername(String token) {
    return extractClaim(token, Claims::getSubject);
  }

  public boolean isTokenValid(String token, UserDetails userDetails) {
    final String username = extractUsername(token);
    return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
  }

  private boolean isTokenExpired(String token) {
    return extractClaim(token, Claims::getExpiration).before(new Date());
  }


  private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
    try {
      Claims claims = Jwts.parser()
        .verifyWith(KEY)
        .build()
        .parseSignedClaims(token)
        .getPayload();

      return claimsResolver.apply(claims);
    } catch (JwtException | IllegalArgumentException e) {
      return null;
    }
  }
}
