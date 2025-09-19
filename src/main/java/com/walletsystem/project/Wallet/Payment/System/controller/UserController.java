package com.walletsystem.project.Wallet.Payment.System.controller;

import com.walletsystem.project.Wallet.Payment.System.entity.User;
import com.walletsystem.project.Wallet.Payment.System.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;

    // 🟢 Get all users (with active status)
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userRepository.findAll());
    }

    // 🟢 Freeze user
    @PutMapping("/{id}/freeze")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> freezeUser(@PathVariable Long id) {
        return userRepository.findById(id).map(user -> {
            user.setActive(false);
            userRepository.save(user);
            return ResponseEntity.ok("User " + user.getUsername() + " has been frozen.");
        }).orElse(ResponseEntity.notFound().build());
    }

    // 🟢 Unfreeze user
    @PutMapping("/{id}/unfreeze")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> unfreezeUser(@PathVariable Long id) {
        return userRepository.findById(id).map(user -> {
            user.setActive(true);
            userRepository.save(user);
            return ResponseEntity.ok("User " + user.getUsername() + " has been unfrozen.");
        }).orElse(ResponseEntity.notFound().build());
    }
}
