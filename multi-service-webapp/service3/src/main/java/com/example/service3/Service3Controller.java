package com.example.service3;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Service3Controller {

    @GetMapping("/")
    public String home() {
        return "Welcome to Service 3\nThis is the home page for Service 3";
    }

    @GetMapping("/hello")
    public String hello() {
        return "Hello from Service 3!";
    }
}
