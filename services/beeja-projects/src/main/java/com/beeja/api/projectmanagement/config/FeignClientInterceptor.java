package com.beeja.api.projectmanagement.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * Feign client interceptor that propagates the {@code Authorization} header
 * from the incoming HTTP request to outgoing Feign client requests.
 */
@Component
public class FeignClientInterceptor implements RequestInterceptor {

  /**
   * Intercepts a Feign request and adds the {@code Authorization} header
   * from the current HTTP request if available.
   * @param template the Feign request template to modify
   */
  @Override
  public void apply(RequestTemplate template) {
    String token = getRequestToken();
    if (token != null) {
      template.header("authorization", token);
    }
  }

  /**
   * Retrieves the {@code Authorization} header from the current HTTP request.
   * @return the token value if present; {@code null} otherwise
   */
  private String getRequestToken() {
    ServletRequestAttributes attributes =
            (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
    if (attributes != null) {
      return attributes.getRequest().getHeader("authorization");
    }
    return null;
  }
}
