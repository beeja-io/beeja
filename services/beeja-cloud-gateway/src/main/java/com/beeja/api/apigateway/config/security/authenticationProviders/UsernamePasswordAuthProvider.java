package com.beeja.api.apigateway.config.security.authenticationProviders;

import com.beeja.api.apigateway.config.security.AuthenticationProvider;
import com.beeja.api.apigateway.utils.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.authentication.ServerAuthenticationFailureHandler;

@Configuration
@Slf4j
public class UsernamePasswordAuthProvider implements AuthenticationProvider {

  @Autowired private ServerAuthenticationFailureHandler authenticationFailureHandler;

  @Override
  public void configure(ServerHttpSecurity serverHttpSecurity) {
    log.info(Constants.REGISTERED_USERNAME_PASSWORD_PROVIDER);
    serverHttpSecurity
        .authorizeExchange(
            authorizeExchangeSpec ->
                authorizeExchangeSpec
                    .pathMatchers(HttpMethod.OPTIONS)
                    .permitAll()
                    .pathMatchers(
                        "/login",
                        "/auth/login/**",
                        "/actuator/**",
                        "/static/favicon.ico",
                        "/favicon.ico",
                        "/auth/login/error",
                        "/auth/logout")
                    .permitAll()
                    .anyExchange()
                    .authenticated())
        .csrf(ServerHttpSecurity.CsrfSpec::disable)
        .formLogin(
            formLoginSpec ->
                formLoginSpec.authenticationFailureHandler(authenticationFailureHandler));
  }

  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }
}
