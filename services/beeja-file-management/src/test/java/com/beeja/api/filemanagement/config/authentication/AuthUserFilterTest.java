package com.beeja.api.filemanagement.config.authentication;


import static org.mockito.Mockito.*;

import com.beeja.api.filemanagement.repository.FileRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

public class AuthUserFilterTest {

  @Mock private FileRepository fileRepository;

  @Mock private AuthUrlProperties authUrlProperties;

  @Mock private RestTemplate restTemplate;

  @InjectMocks private AuthUserFilter authUserFilter;

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.initMocks(this);
  }

  @Test
  public void testDoFilterInternalDenyAccessForProtectedEndpointWithInvalidToken()
      throws ServletException, IOException {
    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);
    FilterChain filterChain = mock(FilterChain.class);

    when(request.getRequestURI()).thenReturn("/ggg");

    String mockResponse = "Mocked Response";
    ResponseEntity<String> mockResponseEntity = new ResponseEntity<>(mockResponse, HttpStatus.OK);
    when(restTemplate.getForEntity(any(String.class), any(Class.class)))
        .thenReturn(mockResponseEntity);



    when(response.getWriter()).thenReturn(mock(PrintWriter.class));

    ReflectionTestUtils.invokeMethod(
        authUserFilter, "doFilterInternal", request, response, filterChain);
  }

  @Test
  public void testDoFilterInternalDenyAccessForProtectedEndpointWithValidToken()
      throws ServletException, IOException {
    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);
    FilterChain filterChain = mock(FilterChain.class);

    RestTemplate restTemplate = mock(RestTemplate.class);

    when(request.getRequestURI()).thenReturn("/files/actuator/");
  }
}
