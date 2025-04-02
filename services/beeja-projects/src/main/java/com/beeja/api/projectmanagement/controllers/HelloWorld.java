package com.beeja.api.projectmanagement.controllers;

import com.beeja.api.projectmanagement.utils.UserContext;
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
