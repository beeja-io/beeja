package com.beeja.api.apigateway.config.security;

import com.beeja.api.apigateway.config.security.properties.SkipGatewayFilterRoutesProperty;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

@Component
@Slf4j
public class CustomTokenRelayGatewayFilterFactory
    extends AbstractGatewayFilterFactory<CustomTokenRelayGatewayFilterFactory.Config> {

    @Autowired
    private SkipGatewayFilterRoutesProperty skipGatewayFilterRoutesProperty;



  public CustomTokenRelayGatewayFilterFactory() {
    super(Config.class);

  }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            String path = exchange.getRequest().getPath().toString();
            for (String skipPath : skipGatewayFilterRoutesProperty.getRoutesAsList()) {
                if (path.startsWith(skipPath)) {
                    log.info("Skipping token relay for path: {}", path);
                    return chain.filter(exchange);
                }
            }

            return
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
        };
    }


    public static class Config {}
}
