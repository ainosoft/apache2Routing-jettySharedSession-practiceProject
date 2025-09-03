package com.example.service1;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class rootcontroller {
	  @GetMapping("/")
	    public String home() {
	        return "Service1 is alive!";
	    }
}
