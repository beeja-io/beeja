package com.beeja.api.projectmanagement.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;

/**
 * Utility class for handling JSON Web Token (JWT) operations.
 */
public class JwtUtils {

  /**
   * Decodes a JWT token and retrieves the claims.
   * @param jwtToken the JWT token to be decoded
   * @param secret the secret key used to validate the JWT token's signature
   * @return the claims contained within the JWT token
   * @throws Exception if the token cannot be decoded or if an error occurs during parsing
   */
  public static Claims decodeJWT(String jwtToken, String secret) throws Exception {

    try {
      Jws<Claims> claims = Jwts.parser().setSigningKey(secret).parseClaimsJws(jwtToken);
      return claims.getBody();
    } catch (Exception e) {
      throw new Exception(e.getMessage());
    }
  }
}
