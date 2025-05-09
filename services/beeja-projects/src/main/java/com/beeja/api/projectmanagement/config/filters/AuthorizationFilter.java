package com.beeja.api.projectmanagement.config.filters;

import com.beeja.api.projectmanagement.client.AccountClient;
import com.beeja.api.projectmanagement.utils.Constants;
import com.beeja.api.projectmanagement.utils.JwtUtils;
import com.beeja.api.projectmanagement.utils.UserContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Authorization filter that intercepts incoming HTTP requests to validate JWT access tokens
 * and authenticate the user. If valid, user details are set in the {@link UserContext}.
 */
@Slf4j
@Component
public class AuthorizationFilter extends OncePerRequestFilter {

  @Autowired AccountClient accountClient;

  @Autowired JwtProperties jwtProperties;

  /**
   * Filters each incoming HTTP request to validate the JWT token and authenticate the user.
   * @param request     the HTTP request
   * @param response    the HTTP response
   * @param filterChain the filter chain
   * @throws ServletException if a servlet error occurs
   * @throws IOException      if an I/O error occurs
   */
  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {

    if (request.getRequestURI().startsWith("/projects/actuator/")
        || request.getRequestURI().equals("/projects/api-docs/swagger-config")
        || request.getRequestURI().startsWith("/projects/swagger-ui/")
        || request.getRequestURI().startsWith("/projects/openApi.yaml")) {
      filterChain.doFilter(request, response);
      return;
    }

    String accessToken = request.getHeader("authorization");

    accessToken = accessToken.substring(7);
    if (isValidAccessToken(accessToken)) {
      log.info(Constants.USER_SUCCESSFULLY_AUTHENTICATED);
      filterChain.doFilter(request, response);
    } else {
      log.error(Constants.USER_FAILED_AUTHENTICATE);
      response.setStatus(HttpServletResponse.SC_FORBIDDEN);
      response.getWriter().write(Constants.ACCESS_DENIED);
    }
  }

  /**
   * Validates the provided access token.
   * @param accessToken the JWT access token
   * @return {@code true} if the token is valid; {@code false} otherwise
   */
  private boolean isValidAccessToken(String accessToken) {
    try {
      return validateJWT(accessToken);
    } catch (HttpClientErrorException e) {
      log.error("HTTP Error: {}", e.getStatusCode());
      return false;
    } catch (Exception e) {
      log.error("Token Validation Exception: {}", e.getMessage());
      return false;
    }
  }

  /**
   * Validates the JWT token by decoding its claims and checking user presence and active status.
   * @param accessToken the JWT access token
   * @return {@code true} if the token is valid and user is active; {@code false} otherwise
   * @throws Exception if an error occurs during JWT decoding
   */
  private boolean validateJWT(String accessToken) throws Exception {
    Claims claims = JwtUtils.decodeJWT(accessToken, jwtProperties.getSecret());
    String email = claims.get("sub").toString();
    return checkUserPresenceAndSetActive(email, accessToken);
  }

  /**
   * Verifies if the user exists and is active in the system; sets the user context if valid.
   * @param email       the user's email
   * @param accessToken the JWT access token
   * @return {@code true} if user exists and is active; {@code false} otherwise
   */
  private boolean checkUserPresenceAndSetActive(String email, String accessToken) {
    ResponseEntity<LinkedHashMap<String, Object>> userIsPresent =
            (ResponseEntity<LinkedHashMap<String, Object>>) accountClient.getEmployeeByEmail(email);
    LinkedHashMap<String, Object> responseBody = userIsPresent.getBody();
    if (userIsPresent.getStatusCode().is2xxSuccessful() && responseBody != null) {
      Boolean userIsActive = (Boolean) responseBody.get("active");
      if (userIsActive) {
        setLoggedInUser(responseBody, accessToken);
      }
      return userIsActive;
    }
    return false;
  }

  /**
   * Sets the authenticated user's details into the {@link UserContext}.
   * @param responseBody the response body containing user details
   * @param accessToken  the JWT access token
   */
  private void setLoggedInUser(LinkedHashMap<String, Object> responseBody, String accessToken) {
    String email = responseBody.get("email").toString();
    String firstName = responseBody.get("firstName").toString();
    String employeeId = responseBody.get("employeeId").toString();
    Map<String, Object> userOrganization = getUserOrganization(responseBody);
    Set<String> permissions = getPermissions(responseBody);
    UserContext.setLoggedInUser(
            email, firstName, employeeId, userOrganization, permissions, accessToken);
  }

  /**
   * Extracts the user's organization details from the response.
   * @param responseBody the response body containing user details
   * @return a map containing organization details like id, name, and email
   */
  private Map<String, Object> getUserOrganization(LinkedHashMap<String, Object> responseBody) {
    Map<String, Object> userOrganization = new HashMap<>();
    Map<String, Object> organizations = (Map<String, Object>) responseBody.get("organizations");
    if (organizations != null) {
      userOrganization.put(Constants.ID, organizations.get("id"));
      userOrganization.put(Constants.NAME, organizations.get("name"));
      userOrganization.put(Constants.EMAIL, organizations.get("email"));
    }
    return userOrganization;
  }

  /**
   * Extracts the user's permissions from the response.
   * @param responseBody the response body containing user roles and permissions
   * @return a set of permissions assigned to the user
   */
  private Set<String> getPermissions(LinkedHashMap<String, Object> responseBody) {
    Set<String> permissions = new HashSet<>();
    Collection<LinkedHashMap<String, Object>> roles =
            (Collection<LinkedHashMap<String, Object>>) responseBody.get("roles");
    if (roles != null) {
      for (LinkedHashMap<String, Object> role : roles) {
        Collection<String> rolePermissions = (Collection<String>) role.get("permissions");
        if (rolePermissions != null) {
          permissions.addAll(rolePermissions);
        }
      }
    }
    return permissions;
  }
}
