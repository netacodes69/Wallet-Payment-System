package com.walletsystem.project.Wallet.Payment.System.service;

import com.walletsystem.project.Wallet.Payment.System.entity.User;
import com.walletsystem.project.Wallet.Payment.System.entity.Wallet;

import java.math.BigDecimal;

public interface WalletService {

    Wallet topUp(User user, BigDecimal amount);

    Wallet withdraw(User user, BigDecimal amount);

    void transfer(User fromUser, User toUser, BigDecimal amount);

    Wallet getWalletByUserId(Long id);
}