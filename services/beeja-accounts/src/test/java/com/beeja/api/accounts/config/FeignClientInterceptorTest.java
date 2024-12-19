package com.beeja.api.accounts.config;

import com.beeja.api.accounts.utils.Constants;
import feign.RequestTemplate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

class FeignClientInterceptorTest {

  @InjectMocks private FeignClientInterceptor feignClientInterceptor;

  @Mock private RequestAttributes requestAttributes;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.initMocks(this);
  }

  @Test
  void testapply() {

    // Arrange
    MockHttpServletRequest mockRequest = new MockHttpServletRequest();
    mockRequest.addHeader(Constants.COOKIE_ACCESS_TOKEN, "testToken");

    ServletRequestAttributes servletRequestAttributes = new ServletRequestAttributes(mockRequest);
    RequestContextHolder.setRequestAttributes(servletRequestAttributes);

    RequestTemplate requestTemplate = new RequestTemplate();

    // Act
    feignClientInterceptor.apply(requestTemplate);
  }
}
