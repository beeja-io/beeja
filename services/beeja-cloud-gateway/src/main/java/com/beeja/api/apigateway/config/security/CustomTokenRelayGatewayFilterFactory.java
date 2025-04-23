package com.beeja.api.apigateway.config.security;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.stereotype.Component;

@Component
public class CustomTokenRelayGatewayFilterFactory
    extends AbstractGatewayFilterFactory<CustomTokenRelayGatewayFilterFactory.Config> {

  public CustomTokenRelayGatewayFilterFactory() {
    super(Config.class);
  }

  @Override
  public GatewayFilter apply(Config config) {
    return (exchange, chain) ->
        ReactiveSecurityContextHolder.getContext()
            .map(SecurityContext::getAuthentication)
            .flatMap(
                authentication -> {
                  if (authentication instanceof UsernamePasswordAuthenticationToken) {
                    String token = (String) authentication.getCredentials();
                    ServerHttpRequest mutatedRequest =
                        exchange
                            .getRequest()
                            .mutate()
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                            .build();
                    return chain.filter(exchange.mutate().request(mutatedRequest).build());
                  } else {
                    return chain.filter(exchange);
                  }
                });
  }

  public static class Config {
      // skip checkstyle
  }
}
