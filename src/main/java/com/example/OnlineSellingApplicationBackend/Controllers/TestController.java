package com.example.OnlineSellingApplicationBackend.Controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController

public class TestController {
    @GetMapping("/api/test/ping")
    public String ping() {
        return "pong";
    }
}