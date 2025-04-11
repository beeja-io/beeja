package com.beeja.api.filemanagement.config.authentication;

import com.beeja.api.filemanagement.client.AccountClient;
import com.beeja.api.filemanagement.utils.Constants;
import com.beeja.api.filemanagement.utils.JwtUtils;
import com.beeja.api.filemanagement.utils.UserContext;
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
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@Slf4j
public class AuthUserFilter extends OncePerRequestFilter {

  @Autowired AccountClient accountClient;

  @Autowired AuthUrlProperties authUrlProperties;

  @Autowired JwtProperties jwtProperties;

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {

    if (request.getRequestURI().startsWith("/files/actuator/")
        || request.getRequestURI().equals("/files/api-docs/swagger-config")
        || request.getRequestURI().startsWith("/files/swagger-ui/")
        || request.getRequestURI().startsWith("/files/openApi.yaml")) {
      filterChain.doFilter(request, response);
      return;
    }

    String accessToken = request.getHeader(Constants.ACCESS_TOKEN_HEADER);

    if (accessToken == null) {
      response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
      response.getWriter().write(Constants.NO_ACCESS_TOKEN_ERROR);
      return;
    }

    accessToken = accessToken.substring(7);
    if (isValidAccessToken(accessToken)) {
      filterChain.doFilter(request, response);
    } else {
      response.setStatus(HttpServletResponse.SC_FORBIDDEN);
      response.getWriter().write(Constants.TOKEN_VERIFICATION_SUCCESSFULLY_FAILED_ERROR);
    }
  }

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

  private boolean validateJWT(String accessToken) throws Exception {
    Claims claims = JwtUtils.decodeJWT(accessToken, jwtProperties.getSecret());
    String email = claims.get("sub").toString();
    return checkUserPresenceAndSetActive(email);
  }

  private boolean checkUserPresenceAndSetActive(String email) {
    ResponseEntity<LinkedHashMap<String, Object>> userIsPresent =
        (ResponseEntity<LinkedHashMap<String, Object>>) accountClient.getUserByEmail(email);
    LinkedHashMap<String, Object> responseBody = userIsPresent.getBody();
    if (userIsPresent.getStatusCode().is2xxSuccessful() && responseBody != null) {
      Boolean userIsActive = (Boolean) responseBody.get("active");
      if (Boolean.TRUE.equals(userIsActive)) {
        setLoggedInUser(responseBody);
      }
      return userIsActive;
    }
    return false;
  }

  private void setLoggedInUser(LinkedHashMap<String, Object> responseBody) {
    String email = responseBody.get("email").toString();
    String firstName = responseBody.get("firstName").toString();
    String employeeId = responseBody.get("employeeId").toString();
    Map<String, Object> userOrganization = getUserOrganization(responseBody);
    Set<String> permissions = getPermissions(responseBody);
    UserContext.setLoggedInUser(email, firstName, employeeId, userOrganization, permissions);
  }

  private Map<String, Object> getUserOrganization(LinkedHashMap<String, Object> responseBody) {
    Map<String, Object> userOrganization = new HashMap<>();
    Map<String, Object> organizations = (Map<String, Object>) responseBody.get("organizations");
    if (organizations != null) {
      userOrganization.put("id", organizations.get("id"));
      userOrganization.put("name", organizations.get("name"));
      userOrganization.put("email", organizations.get("email"));
    }
    return userOrganization;
  }

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

  private String getEmailFromAccessToken(String accessTokenAttributes) {
    ObjectMapper objectMapper = new ObjectMapper();
    try {
      JsonNode accessTokenJson = objectMapper.readTree(accessTokenAttributes);
      return accessTokenJson.get("email").asText();
    } catch (IOException e) {
      log.error("Error reading access token attributes: {}", e.getMessage());
      return null;
    }
  }

  private String getIssuedClientId(String accessTokenAttributes) {
    ObjectMapper objectMapper = new ObjectMapper();
    try {
      JsonNode accessTokenJson = objectMapper.readTree(accessTokenAttributes);
      return accessTokenJson.get("azp").asText();
    } catch (IOException e) {
      log.error("Error reading access token attributes: {}", e.getMessage());
      return null;
    }
  }
}
