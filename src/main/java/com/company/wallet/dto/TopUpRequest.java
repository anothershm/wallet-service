package com.company.wallet.dto;

import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class TopUpRequest {
    private BigDecimal amount;
    String creditCardNumber;

}