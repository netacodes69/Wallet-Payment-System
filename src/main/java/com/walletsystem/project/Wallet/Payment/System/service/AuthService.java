package com.walletsystem.project.Wallet.Payment.System.service;

import com.walletsystem.project.Wallet.Payment.System.dto.LoginRequest;
import com.walletsystem.project.Wallet.Payment.System.dto.SignupRequest;
import org.springframework.http.ResponseEntity;


public interface AuthService {
    ResponseEntity<?> register(SignupRequest request);
    ResponseEntity<?> login(LoginRequest request);
}