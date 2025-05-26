package com.beeja.api.apigateway.controllers;

import com.beeja.api.apigateway.config.security.properties.AuthProperties;
import java.net.URI;
import java.time.Duration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/auth")
public class AuthController {

  @Autowired AuthProperties authProperties;

  @GetMapping("/logout")
  public Mono<Void> logout(
      ServerWebExchange exchange, @RequestParam(value = "error", required = false) String error) {
    ServerHttpResponse response = exchange.getResponse();
    SecurityContextHolder.clearContext();
    response.addCookie(ResponseCookie.from("SESSION", "").maxAge(Duration.ZERO).path("/").build());
    response.setStatusCode(HttpStatus.FOUND);
    if (error != null && !error.isEmpty()) {
      response.getHeaders().setLocation(URI.create("/login?error=failed"));
    } else {
      response.getHeaders().setLocation(URI.create("/login"));
    }
    return response.setComplete();
  }


  @GetMapping("/login")
  public ResponseEntity<Object> login() {
    return ResponseEntity.status(HttpStatus.TEMPORARY_REDIRECT)
        .location(URI.create(authProperties.getFrontEndUrl()))
        .build();
  }
}
