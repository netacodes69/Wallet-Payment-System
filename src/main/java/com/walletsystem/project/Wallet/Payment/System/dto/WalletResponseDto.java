package com.walletsystem.project.Wallet.Payment.System.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record WalletResponseDto(
        Long id,
        BigDecimal balance,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}