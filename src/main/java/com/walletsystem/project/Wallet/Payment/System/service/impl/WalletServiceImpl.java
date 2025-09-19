package com.walletsystem.project.Wallet.Payment.System.service.impl;

import com.walletsystem.project.Wallet.Payment.System.entity.User;
import com.walletsystem.project.Wallet.Payment.System.entity.Wallet;
import com.walletsystem.project.Wallet.Payment.System.entity.WalletTransaction;
import com.walletsystem.project.Wallet.Payment.System.repository.WalletRepository;
import com.walletsystem.project.Wallet.Payment.System.repository.WalletTransactionRepository;
import com.walletsystem.project.Wallet.Payment.System.service.WalletService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class WalletServiceImpl implements WalletService {

    private final WalletRepository walletRepository;
    private final WalletTransactionRepository transactionRepository;

    @Override
    @Transactional
    public Wallet topUp(User user, BigDecimal amount) {
        Wallet wallet = walletRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Wallet not found"));
        wallet.setBalance(wallet.getBalance().add(amount));
        walletRepository.save(wallet);

        transactionRepository.save(WalletTransaction.builder()
                .wallet(wallet)
                .type("TOPUP")
                .amount(amount)
                .note("Top-up")
                .build());

        return wallet;
    }

    @Override
    @Transactional
    public Wallet withdraw(User user, BigDecimal amount) {
        Wallet wallet = walletRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Wallet not found"));

        if (wallet.getBalance().compareTo(amount) < 0) {
            throw new RuntimeException("Insufficient balance");
        }

        wallet.setBalance(wallet.getBalance().subtract(amount));
        walletRepository.save(wallet);

        transactionRepository.save(WalletTransaction.builder()
                .wallet(wallet)
                .type("WITHDRAW")
                .amount(amount)
                .note("Withdraw")
                .build());

        return wallet;
    }

    @Override
    @Transactional
    public void transfer(User fromUser, User toUser, BigDecimal amount) {
        Wallet fromWallet = walletRepository.findByUserId(fromUser.getId())
                .orElseThrow(() -> new RuntimeException("Sender wallet not found"));
        Wallet toWallet = walletRepository.findByUserId(toUser.getId())
                .orElseThrow(() -> new RuntimeException("Receiver wallet not found"));

        if (fromWallet.getBalance().compareTo(amount) < 0) {
            throw new RuntimeException("Insufficient balance");
        }

        fromWallet.setBalance(fromWallet.getBalance().subtract(amount));
        walletRepository.save(fromWallet);

        toWallet.setBalance(toWallet.getBalance().add(amount));
        walletRepository.save(toWallet);

        // log transactions for both wallets
        transactionRepository.save(WalletTransaction.builder()
                .wallet(fromWallet)
                .type("TRANSFER")
                .amount(amount)
                .note("Transferred to userId " + toUser.getId())
                .build());

        transactionRepository.save(WalletTransaction.builder()
                .wallet(toWallet)
                .type("TRANSFER")
                .amount(amount)
                .note("Received from userId " + fromUser.getId())
                .build());
    }
    @Override
    public Wallet getWalletByUserId(Long userId) {
        return walletRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Wallet not found"));
    }
}