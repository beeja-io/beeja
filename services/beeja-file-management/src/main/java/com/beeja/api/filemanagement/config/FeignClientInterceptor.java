package com.beeja.api.filemanagement.config;

import com.beeja.api.filemanagement.utils.Constants;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import feign.codec.Decoder;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.support.ResponseEntityDecoder;
import org.springframework.cloud.openfeign.support.SpringDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Component
public class FeignClientInterceptor implements RequestInterceptor {

  @Override
  public void apply(RequestTemplate template) {
    String token = getRequestToken();
    if (token != null) {
      template.header(Constants.ACCESS_TOKEN_HEADER, token);
    }
  }

  private String getRequestToken() {
    ServletRequestAttributes attributes =
        (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
    if (attributes != null) {
      return attributes.getRequest().getHeader(Constants.ACCESS_TOKEN_HEADER);
    }
    return null;
  }

  @Bean
  public Decoder feignDecoder() {
    return new ResponseEntityDecoder(new SpringDecoder(feignHttpMessageConverter()));
  }

  private ObjectFactory<HttpMessageConverters> feignHttpMessageConverter() {
    return () -> new HttpMessageConverters();
  }
}
