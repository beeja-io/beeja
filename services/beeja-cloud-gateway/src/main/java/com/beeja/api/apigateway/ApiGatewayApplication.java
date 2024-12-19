package com.beeja.api.apigateway;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.gateway.filter.factory.TokenRelayGatewayFilterFactory;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableDiscoveryClient
public class ApiGatewayApplication {

  public static void main(String[] args) {
    SpringApplication.run(ApiGatewayApplication.class, args);
  }

  @Autowired private TokenRelayGatewayFilterFactory filterFactory;

  @Value("${resource-server-urls.accounts-service}")
  private String accountsURL;

  @Value("${resource-server-urls.employee-service}")
  private String employeesURL;

  @Value("${resource-server-urls.expense-service}")
  private String expenseURL;

  @Value("${resource-server-urls.finance-service}")
  private String financeURL;

  @Bean
  public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
    return builder
        .routes()
        .route(
            "account-service",
            r ->
                r.path("/accounts")
                    .filters(f -> f.filters(filterFactory.apply()).removeRequestHeader("Cookie"))
                    .uri(accountsURL))
        .route(
            "employee-service",
            r ->
                r.path("/employees")
                    .filters(f -> f.filters(filterFactory.apply()).removeRequestHeader("Cookie"))
                    .uri(employeesURL))
        .route(
            "expense-service",
            r ->
                r.path("/expenses")
                    .filters(f -> f.filters(filterFactory.apply()).removeRequestHeader("Cookie"))
                    .uri(expenseURL))
        .route(
            "finance-service",
            r ->
                r.path("/finance")
                    .filters(f -> f.filters(filterFactory.apply()).removeRequestHeader("Cookie"))
                    .uri(financeURL))
        .build();
  }
}
