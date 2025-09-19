package com.walletsystem.project.Wallet.Payment.System.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class TransactionSummaryDto {
    private String type;
    private BigDecimal amount;
    private String note;
    private LocalDateTime createdAt;
}