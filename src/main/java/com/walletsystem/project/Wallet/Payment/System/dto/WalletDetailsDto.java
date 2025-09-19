package com.walletsystem.project.Wallet.Payment.System.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
public class WalletDetailsDto {
    private BigDecimal balance;
    private List<TransactionSummaryDto> lastTransactions;
}