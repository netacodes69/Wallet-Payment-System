package com.walletsystem.project.Wallet.Payment.System.controller;

import com.walletsystem.project.Wallet.Payment.System.dto.LoginRequest;
import com.walletsystem.project.Wallet.Payment.System.dto.SignupRequest;
import com.walletsystem.project.Wallet.Payment.System.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signupRequest) {
        return authService.register(signupRequest);
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        return authService.login(loginRequest);
    }
}
