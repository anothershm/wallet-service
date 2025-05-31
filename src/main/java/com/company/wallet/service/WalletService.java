package com.company.wallet.service;

import com.company.wallet.exception.WalletNotFoundException;
import com.company.wallet.model.Wallet;
import com.company.wallet.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WalletService {

    private final WalletRepository walletRepository;
    private final StripeService stripeService;

    public Wallet getWallet(UUID walletId) {
        return walletRepository.findById(walletId)
                .orElseThrow(() -> new WalletNotFoundException(walletId));
    }

    public Wallet topUp(UUID walletId, BigDecimal amount, String creditCardNumber) {
        Wallet wallet = getWallet(walletId);
        wallet.topUp(amount);
        Wallet updatedWallet = walletRepository.save(wallet);

        // Here I think of some type of retry mechanism in case of failure
        stripeService.charge(creditCardNumber, amount);

        return updatedWallet;
    }
}
