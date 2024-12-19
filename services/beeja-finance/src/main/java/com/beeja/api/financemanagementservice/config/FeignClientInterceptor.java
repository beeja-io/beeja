package com.beeja.api.financemanagementservice.config;

import com.beeja.api.financemanagementservice.Utils.Constants;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Component
public class FeignClientInterceptor implements RequestInterceptor {

  @Override
  public void apply(RequestTemplate template) {
    String token = getRequestToken();
    if (token != null) {
      template.header(Constants.AUTHORIZATION, token);
    }
  }

  private String getRequestToken() {
    ServletRequestAttributes attributes =
        (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
    if (attributes != null) {
      return attributes.getRequest().getHeader(Constants.AUTHORIZATION);
    }
    return null;
  }
}
