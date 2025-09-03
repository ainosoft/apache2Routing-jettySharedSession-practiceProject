package com.example.service2;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Service2Controller {

    @GetMapping("/")
    public String home() {
        return "Welcome to Service 2\nThis is the home page for Service 2";
    }

    @GetMapping("/hello")
    public String hello() {
        return "Hello from Service 2!";
    }
}
