package com.beeja.api.apigateway.controllers;

import com.beeja.api.apigateway.config.security.properties.AuthProperties;
import java.net.URI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Home {
  @Autowired AuthProperties authProperties;

  @GetMapping("/")
  public ResponseEntity<Object> login() {
    return ResponseEntity.status(HttpStatus.TEMPORARY_REDIRECT)
        .location(URI.create(authProperties.getFrontEndUrl()))
        .build();
    
  }
}
