package com.beeja.api.accounts.config.filters;

import com.beeja.api.accounts.config.properties.SecretProperties;
import com.beeja.api.accounts.model.Organization.Organization;
import com.beeja.api.accounts.model.Organization.Role;
import com.beeja.api.accounts.model.User;
import com.beeja.api.accounts.repository.UserRepository;
import com.beeja.api.accounts.utils.Constants;
import com.beeja.api.accounts.utils.UserContext;
import com.beeja.common.backend_common.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

@Component
public class AuthUserFilter extends OncePerRequestFilter {

  @Autowired UserRepository userRepository;

  @Autowired JwtProperties jwtProperties;

  @Autowired SecretProperties secretProperties;

  private static String responseMessage;

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {

    if (request.getRequestURI().startsWith("/accounts/actuator/")
        || request.getRequestURI().equals("/accounts/api-docs/swagger-config")
        || request.getRequestURI().startsWith("/accounts/swagger-ui/")
        || request.getRequestURI().startsWith("/accounts/openApi.yaml")
        || request.getRequestURI().startsWith("/accounts/public")) {
      logger.info(Constants.CAME_TO_PUBLIC_ROUTE + request.getRequestURI());
      filterChain.doFilter(request, response);
      return;
    }

    String accessToken = request.getHeader(Constants.COOKIE_ACCESS_TOKEN);

    if (accessToken == null) {
      response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
      response.getWriter().write(Constants.NOT_AUTHORISED);
      return;
    }

    accessToken = accessToken.substring(7);
    if (isValidAccessToken(accessToken)) {
      filterChain.doFilter(request, response);
    } else {
      Cookie cookie = new Cookie("SESSION", "");
      cookie.setPath("/");
      cookie.setMaxAge(0);
      response.addCookie(cookie);
      response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
      response.getWriter().write(Constants.ACCESS_DENIED + ", " + responseMessage);
    }
  }

  private boolean isValidAccessToken(String accessToken) {
    try {
      if (accessToken.equals(secretProperties.getOpenEndpointToken())) {
        return true;
      }
      Claims claims = JwtUtils.decodeJWT(accessToken, jwtProperties.getSecret());
      String email = claims.get("sub").toString();
      User user = userRepository.findByEmail(email);

      if (user != null && user.isActive()) {
        Set<String> permissions = new HashSet<>();
        for (Role role : user.getRoles()) {
          permissions.addAll(role.getPermissions());
        }

        Organization organization = user.getOrganizations();
        UserContext.setLoggedInUser(
            email,
            user.getFirstName(),
            organization,
            user.getEmployeeId(),
            user.getRoles(),
            permissions,
            accessToken);
        return true;
      } else {
        responseMessage = "Invalid User";
        return false;
      }
    } catch (Exception e) {
      responseMessage = e.getMessage();
      return false;
    }
  }
}
