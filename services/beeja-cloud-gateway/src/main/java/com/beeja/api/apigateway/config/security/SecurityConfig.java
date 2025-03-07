package com.beeja.api.apigateway.config.security;

import static org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers.pathMatchers;

import com.beeja.api.apigateway.config.security.properties.AuthProperties;
import java.time.Duration;
import java.util.List;
import java.util.ServiceLoader;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.ServerAuthenticationFailureHandler;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import org.springframework.web.server.session.CookieWebSessionIdResolver;
import reactor.core.publisher.Mono;

@Configuration
public class SecurityConfig {

  @Autowired AuthProperties authProperties;


  @Autowired private ServerAuthenticationFailureHandler authenticationFailureHandler;

  private static final ServerWebExchangeMatcher ACCOUNTS_MATCHERS =
      pathMatchers(
          "GET",
          "/accounts/swagger-ui/**",
          "/accounts/api-docs/**",
          "/accounts/openApi.yaml",
          "/accounts/api-docs/swagger-config",
          "/accounts/actuator/**");

  private static final ServerWebExchangeMatcher EMPLOYEE_MATCHER =
      pathMatchers(
          "GET",
          "/employees/swagger-ui/**",
          "/employees/api-docs/**",
          "/employees/openApi.yaml",
          "/employees/api-docs/swagger-config",
          "/employees/actuator/**");

  private static final ServerWebExchangeMatcher FILE_SERVICE_MATCHER =
      pathMatchers(
          "GET",
          "/files/swagger-ui/**",
          "/files/api-docs/**",
          "/files/openApi.yaml",
          "/files/api-docs/swagger-config",
          "/files/actuator/**");

  private static final ServerWebExchangeMatcher EXPENSE_SERVICE_MATCHER =
      pathMatchers(
          "GET",
          "/expenses/swagger-ui/**",
          "/expenses/api-docs/**",
          "/expenses/openApi.yaml",
          "/expenses/api-docs/swagger-config",
          "/expenses/actuator/**");

  private static final ServerWebExchangeMatcher FINANCE_SERVICE_MATCHER =
      pathMatchers(
          "GET",
          "/finance/swagger-ui/**",
          "/finance/api-docs/**",
          "/finance/openApi.yaml",
          "/finance/api-docs/swagger-config",
          "/finance/actuator/**");

  @Bean
  @Order(1)
  public SecurityWebFilterChain accountsSecurity(ServerHttpSecurity httpSecurity) {
    httpSecurity
        .securityMatcher(ACCOUNTS_MATCHERS)
        .authorizeExchange(
            authorizeExchangeSpec -> authorizeExchangeSpec.anyExchange().permitAll());
    return httpSecurity.build();
  }

  @Bean
  @Order(1)
  public SecurityWebFilterChain employeeSecurity(ServerHttpSecurity httpSecurity) {
    httpSecurity
        .securityMatcher(EMPLOYEE_MATCHER)
        .authorizeExchange(
            authorizeExchangeSpec -> authorizeExchangeSpec.anyExchange().permitAll());
    return httpSecurity.build();
  }

  @Bean
  @Order(1)
  public SecurityWebFilterChain fileServiceSecurity(ServerHttpSecurity httpSecurity) {
    httpSecurity
        .securityMatcher(FILE_SERVICE_MATCHER)
        .authorizeExchange(
            authorizeExchangeSpec -> authorizeExchangeSpec.anyExchange().permitAll());
    return httpSecurity.build();
  }

  @Bean
  @Order(1)
  public SecurityWebFilterChain expenseServiceSecurity(ServerHttpSecurity httpSecurity) {
    httpSecurity
        .securityMatcher(EXPENSE_SERVICE_MATCHER)
        .authorizeExchange(
            authorizeExchangeSpec -> authorizeExchangeSpec.anyExchange().permitAll());
    return httpSecurity.build();
  }

  @Bean
  @Order(1)
  public SecurityWebFilterChain financeServiceSecurity(ServerHttpSecurity httpSecurity) {
    httpSecurity
        .securityMatcher(FINANCE_SERVICE_MATCHER)
        .authorizeExchange(
            authorizeExchangeSpec -> authorizeExchangeSpec.anyExchange().permitAll());
    return httpSecurity.build();
  }

  @Bean
  public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity serverHttpSecurity) {
    serverHttpSecurity.exceptionHandling(
            exceptionHandlingSpec ->
                    exceptionHandlingSpec.authenticationEntryPoint(
                            ((exchange, ex) -> {
                              exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                              return Mono.empty();
                            })));
    ServiceLoader<AuthenticationProvider> loader = ServiceLoader.load(AuthenticationProvider.class);

    for (AuthenticationProvider provider : loader) {
      provider.configure(serverHttpSecurity);
    }

    return serverHttpSecurity.build();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }


  @Bean
  public CorsWebFilter corsWebFilter() {
    CorsConfiguration corsConfig = new CorsConfiguration();
    corsConfig.setAllowedOriginPatterns(List.of(authProperties.getFrontEndUrl(), authProperties.getLocalFrontEndUrl()));
    corsConfig.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
    corsConfig.setAllowedHeaders(List.of("*"));
    corsConfig.setAllowCredentials(true);

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", corsConfig);

    return new CorsWebFilter(source);
  }


  @Bean
  public CookieWebSessionIdResolver cookieSessionIdResolverWithoutSameSite() {
    var resolver = new CookieWebSessionIdResolver();
    resolver.addCookieInitializer(
        builder -> builder.sameSite("None").secure(true).maxAge(Duration.ofDays(2)));
    return resolver;
  }
}
