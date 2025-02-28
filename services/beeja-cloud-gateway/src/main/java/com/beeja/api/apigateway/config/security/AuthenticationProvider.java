package com.beeja.api.apigateway.config.security;


import org.springframework.security.config.web.server.ServerHttpSecurity;

public interface AuthenticationProvider {
    void configure(ServerHttpSecurity httpSecurity);
}