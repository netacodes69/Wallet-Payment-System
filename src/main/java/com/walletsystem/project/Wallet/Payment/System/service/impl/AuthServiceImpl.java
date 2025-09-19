package com.walletsystem.project.Wallet.Payment.System.service.impl;

import com.walletsystem.project.Wallet.Payment.System.dto.LoginRequest;
import com.walletsystem.project.Wallet.Payment.System.dto.SignupRequest;
import com.walletsystem.project.Wallet.Payment.System.entity.User;
import com.walletsystem.project.Wallet.Payment.System.entity.Wallet;
import com.walletsystem.project.Wallet.Payment.System.repository.UserRepository;
import com.walletsystem.project.Wallet.Payment.System.repository.WalletRepository;
import com.walletsystem.project.Wallet.Payment.System.security.JwtUtil;
import com.walletsystem.project.Wallet.Payment.System.service.AuthService;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;

@Service
@AllArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final WalletRepository walletRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Transactional
    @Override
    public ResponseEntity<?> register(SignupRequest request) {

        if (userRepository.existsByUsername(request.getUsername())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", "Username is already taken"));
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", "Email is already in use"));
        }

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role("ROLE_USER")
                .build();

        User savedUser = userRepository.save(user);

        // 🟢 Auto-create wallet for new user
        Wallet wallet = Wallet.builder()
                .user(savedUser)
                .balance(BigDecimal.ZERO)
                .build();

        walletRepository.save(wallet);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("message", "User registered successfully"));
    }

    /**
     * Handles user login and generates a JWT token.
     */
    @Override
    public ResponseEntity<?> login(LoginRequest request) {
        Optional<User> opt = userRepository.findByUsernameOrEmail(
                request.getUsernameOrEmail(),
                request.getUsernameOrEmail()
        );

        if (opt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Invalid credentials"));
        }

        User user = opt.get();
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Invalid credentials"));
        }

        String token = jwtUtil.generateToken(user.getUsername(), user.getRole());

        return ResponseEntity.ok(Map.of(
                "accessToken", token,
                "tokenType", "Bearer"
        ));
    }
}
