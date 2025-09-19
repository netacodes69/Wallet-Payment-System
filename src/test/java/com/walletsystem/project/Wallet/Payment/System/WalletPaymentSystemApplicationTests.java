package com.walletsystem.project.Wallet.Payment.System;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootTest
class WalletPaymentSystemApplicationTests {

     @Test
     void generateHashedPasswordForAdmin() {
         BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
         String rawPassword = "admin123";
         String hashed = encoder.encode(rawPassword);
         System.out.println("Hashed password for 'admin123': " + hashed);
     }
}
