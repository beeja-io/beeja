package com.beeja.api.apigateway.utils;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.util.Date;

public class JwtUtil {

  private static final long EXPIRATION_TIME = 864_000_000;

  public static String generateToken(String email, String key) {
    return Jwts.builder()
        .setSubject(email)
        .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
        .signWith(SignatureAlgorithm.HS256, key)
        .compact();
  }

  public static String extractUsername(String token, String key) {
    return Jwts.parser().setSigningKey(key).parseClaimsJws(token).getBody().getSubject();
  }
}
