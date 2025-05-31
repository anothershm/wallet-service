package com.company.wallet.service;

import com.company.wallet.dto.Payment;
import com.company.wallet.exception.WalletNotFoundException;
import com.company.wallet.model.Wallet;
import com.company.wallet.repository.WalletRepository;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

class WalletServiceTest {

    private static final UUID A_WALLET_ID = UUID.randomUUID();
    private static final String A_CREDIT_CARD_NUMBER = "1234 5678 9012 3456";
    private static final BigDecimal A_TOP_UP_AMOUNT = new BigDecimal("25.00");

    private final WalletRepository walletRepository = mock(WalletRepository.class);
    private final StripeService stripeService = mock(StripeService.class);
    private final WalletService underTest = new WalletService(walletRepository, stripeService);

    @Test
    void when_we_get_wallet_by_an_id_that_exists_then_its_recovered() {
        Wallet expectedWallet = new Wallet();
        expectedWallet.setId(A_WALLET_ID);

        when(walletRepository.findById(A_WALLET_ID)).thenReturn(Optional.of(expectedWallet));

        Wallet result = underTest.getWallet(A_WALLET_ID);

        assertEquals(expectedWallet, result);
        verifyNoInteractions(stripeService);
    }

    @Test
    void get_wallet_should_throw_when_not_found() {
        when(walletRepository.findById(A_WALLET_ID)).thenReturn(Optional.empty());

        assertThrows(WalletNotFoundException.class, () -> underTest.getWallet(A_WALLET_ID));
        verifyNoInteractions(stripeService);
    }

    @Test
    void topUp_should_increase_balance_and_save_wallet() {
        Wallet wallet = new Wallet();
        wallet.setId(A_WALLET_ID);
        wallet.setBalance(BigDecimal.TEN);

        when(walletRepository.findById(A_WALLET_ID)).thenReturn(Optional.of(wallet));
        when(walletRepository.save(wallet)).thenAnswer(i -> i.getArgument(0));
        when(stripeService.charge(A_CREDIT_CARD_NUMBER, A_TOP_UP_AMOUNT)).thenReturn(new Payment("payment_123"));

        Wallet result = underTest.topUp(A_WALLET_ID, A_TOP_UP_AMOUNT, A_CREDIT_CARD_NUMBER);

        assertEquals(new BigDecimal("35.00"), result.getBalance());
        verify(walletRepository).save(wallet);
    }
}