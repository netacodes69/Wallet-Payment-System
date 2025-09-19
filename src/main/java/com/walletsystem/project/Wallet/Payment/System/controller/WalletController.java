package com.walletsystem.project.Wallet.Payment.System.controller;

import com.walletsystem.project.Wallet.Payment.System.dto.TransactionSummaryDto;
import com.walletsystem.project.Wallet.Payment.System.dto.WalletDetailsDto;
import com.walletsystem.project.Wallet.Payment.System.dto.WalletResponseDto;
import com.walletsystem.project.Wallet.Payment.System.entity.User;
import com.walletsystem.project.Wallet.Payment.System.entity.Wallet;
import com.walletsystem.project.Wallet.Payment.System.entity.WalletTransaction;
import com.walletsystem.project.Wallet.Payment.System.repository.UserRepository;
import com.walletsystem.project.Wallet.Payment.System.repository.WalletRepository;
import com.walletsystem.project.Wallet.Payment.System.repository.WalletTransactionRepository;
import com.walletsystem.project.Wallet.Payment.System.service.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/wallet")
@RequiredArgsConstructor
public class WalletController {

    private final WalletService walletService;
    private final UserRepository userRepository;
    private final WalletRepository walletRepository;
    private final WalletTransactionRepository transactionRepository;

    // 🔹 Top-up wallet
    @PostMapping("/topup/{userId}")
    public ResponseEntity<WalletResponseDto> topUp(
            @PathVariable Long userId,
            @RequestParam BigDecimal amount
    ) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!user.isActive()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }

        Wallet wallet = walletService.topUp(user, amount);
        return ResponseEntity.ok(toDto(wallet));
    }

    // 🔹 Withdraw from wallet
    @PostMapping("/withdraw/{userId}")
    public ResponseEntity<WalletResponseDto> withdraw(
            @PathVariable Long userId,
            @RequestParam BigDecimal amount
    ) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!user.isActive()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }

        Wallet wallet = walletService.withdraw(user, amount);
        return ResponseEntity.ok(toDto(wallet));
    }

    // 🔹 Transfer between wallets
    @PostMapping("/transfer")
    public ResponseEntity<String> transfer(
            @RequestParam Long fromUserId,
            @RequestParam Long toUserId,
            @RequestParam BigDecimal amount
    ) {
        User fromUser = userRepository.findById(fromUserId)
                .orElseThrow(() -> new RuntimeException("Sender not found"));
        User toUser = userRepository.findById(toUserId)
                .orElseThrow(() -> new RuntimeException("Receiver not found"));

        if (!fromUser.isActive()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Your account is frozen. Cannot send money.");
        }
        if (!toUser.isActive()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Receiver account is frozen. Cannot receive money.");
        }

        walletService.transfer(fromUser, toUser, amount);
        return ResponseEntity.ok("Transfer successful");
    }

    // 🔹 Wallet Details with last 5 transactions
    @GetMapping("/details/{userId}")
    public ResponseEntity<WalletDetailsDto> getWalletDetails(@PathVariable Long userId) {
        Wallet wallet = walletRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Wallet not found"));

        List<WalletTransaction> transactions = transactionRepository
                .findByWalletIdOrderByCreatedAtDesc(wallet.getId());

        List<TransactionSummaryDto> lastTransactions = transactions.stream()
                .limit(5)
                .map(txn -> new TransactionSummaryDto(
                        txn.getType(),
                        txn.getAmount(),
                        txn.getNote(),
                        txn.getCreatedAt()
                ))
                .toList();

        WalletDetailsDto details = new WalletDetailsDto(wallet.getBalance(), lastTransactions);
        return ResponseEntity.ok(details);
    }

    // 🔹 Get wallet balance
    @GetMapping("/balance/{userId}")
    public ResponseEntity<BigDecimal> getBalance(@PathVariable Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!user.isActive()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }

        Wallet wallet = walletService.getWalletByUserId(user.getId());
        return ResponseEntity.ok(wallet.getBalance());
    }

    // 🔹 Helper method to convert Wallet → WalletResponseDto
    private WalletResponseDto toDto(Wallet wallet) {
        return new WalletResponseDto(
                wallet.getId(),
                wallet.getBalance(),
                wallet.getCreatedAt(),
                wallet.getUpdatedAt()
        );
    }
}