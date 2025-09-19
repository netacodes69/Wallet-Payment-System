package com.walletsystem.project.Wallet.Payment.System.config;

import com.walletsystem.project.Wallet.Payment.System.repository.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class SecurityBeansConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService(UserRepository userRepository) {
        return usernameOrEmail -> userRepository
                .findByUsernameOrEmail(usernameOrEmail, usernameOrEmail)
                .map(user -> org.springframework.security.core.userdetails.User.builder()
                        .username(user.getUsername())
                        .password(user.getPassword())
                        .roles(user.getRole().replace("ROLE_", "")) // Spring expects roles without "ROLE_"
                        .build())
                .orElseThrow(() -> new UsernameNotFoundException(
                        "User not found with username/email: " + usernameOrEmail
                ));
    }
}
