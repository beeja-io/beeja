package com.beeja.api.apigateway.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/** Controller class to handle login related requests. */
@Controller
@Slf4j
public class LoginController {

  /**22`````
   * Handles GET requests to "/login" endpoint and returns the login page.
   *
   * @return The name of the Thymeleaf template to render, in this case, "login".
   */
  @GetMapping("/login")
  public Mono<String> getHomePage(@RequestParam(value = "redirectUrl", required = false) String redirectUrl,
                                  ServerWebExchange exchange, Model model) {

      return exchange.getSession().flatMap(webSession -> {
          String redirect = redirectUrl;

          if (redirect == null || redirect.isBlank()) {
              redirect = exchange.getRequest().getHeaders().getFirst("Referer");
              if (redirect == null || redirect.isBlank()) {
                  redirect = "/";
              }
          }
          webSession.getAttributes().put("redirectUrl", redirect);
          model.addAttribute("redirectUrl", redirect);

          return ReactiveSecurityContextHolder.getContext()
        .map(SecurityContext::getAuthentication)
        .flatMap(
            authentication -> {
              if (authentication != null && authentication.isAuthenticated()) {
                return Mono.just("redirect:/");
              } else {
                return Mono.just("login");
              }
            });
      });

  }
}
