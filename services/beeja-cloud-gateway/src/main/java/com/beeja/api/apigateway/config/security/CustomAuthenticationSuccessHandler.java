package com.beeja.api.apigateway.config.security;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.ServerAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.net.URI;

@Component
@Slf4j
public class CustomAuthenticationSuccessHandler implements ServerAuthenticationSuccessHandler {
    @Override
    public Mono<Void> onAuthenticationSuccess(WebFilterExchange webFilterExchange,
                                              Authentication authentication) {

        // Retrieve session and extract redirectUrl
        return webFilterExchange.getExchange().getSession()
                .flatMap(webSession -> {
                    String redirectUrl = (String) webSession.getAttributes().get("redirectUrl");
                    if (redirectUrl == null || redirectUrl.isBlank()) {
                        redirectUrl = "/";
                    }
                    ServerHttpResponse response = webFilterExchange.getExchange().getResponse();
                    response.setStatusCode(HttpStatus.SEE_OTHER);
                    response.getHeaders().setLocation(URI.create(redirectUrl));
                    return response.setComplete();
                });
    }
}
