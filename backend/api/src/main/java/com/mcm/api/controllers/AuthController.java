package com.mcm.api.controllers;

import com.mcm.api.dto.LoginRequest;
import com.mcm.api.dto.RegisterRequest;
import com.mcm.api.dto.UserResponse;
import com.mcm.api.services.AuthService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public UserResponse register(@RequestBody RegisterRequest req) {
        return authService.register(req);
    }

    @PostMapping("/login")
    public UserResponse login(@RequestBody LoginRequest req) {
        return authService.login(req);
    }
}
