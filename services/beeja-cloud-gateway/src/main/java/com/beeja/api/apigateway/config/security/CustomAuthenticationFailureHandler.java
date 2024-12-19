package com.beeja.api.apigateway.config.security;

import java.net.URI;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.ServerAuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class CustomAuthenticationFailureHandler implements ServerAuthenticationFailureHandler {

  @Override
  public Mono<Void> onAuthenticationFailure(
      WebFilterExchange webFilterExchange, AuthenticationException exception) {
    ServerWebExchange exchange = webFilterExchange.getExchange();
    return Mono.fromRunnable(
        () -> {
          exchange.getResponse().setStatusCode(HttpStatus.FOUND);
          exchange.getResponse().getHeaders().setLocation(URI.create("/login?error=failed"));
        });
  }
}
