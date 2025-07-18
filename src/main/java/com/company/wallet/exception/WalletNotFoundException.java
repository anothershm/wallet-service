package com.company.wallet.exception;

import java.util.UUID;

public class WalletNotFoundException extends RuntimeException {
    public WalletNotFoundException(UUID walletId) {
        super("Wallet with ID " + walletId + " not found.");
    }
}
