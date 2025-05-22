package com.beeja.api.performance.controllers;

import com.beeja.api.performance.utils.UserContext;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1")
public class HelloWorld {

  @GetMapping
  public String getHelloWorld() {
    return "Hello, " + UserContext.getLoggedInUserName();
  }
}
