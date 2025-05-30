package com.beeja.api.performance_management.controllers;

import com.beeja.api.performance_management.utils.UserContext;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1")
public class HelloWorld {

    @GetMapping("/hello")
    public String hello() {
        return "Hello, " + UserContext.getLoggedInUserName();
    }
}
