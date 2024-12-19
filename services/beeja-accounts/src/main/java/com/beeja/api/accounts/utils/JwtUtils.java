package com.beeja.api.accounts.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;

public class JwtUtils {

  public static Claims decodeJWT(String jwtToken, String secret) throws Exception {

    try {
      Jws<Claims> claims = Jwts.parser().setSigningKey(secret).parseClaimsJws(jwtToken);
      return claims.getBody();
    } catch (Exception e) {
      throw new Exception(e.getMessage());
    }
  }
}
