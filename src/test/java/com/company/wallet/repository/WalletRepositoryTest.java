package com.company.wallet.repository;


import com.company.wallet.model.Wallet;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ActiveProfiles("test")
@DataJpaTest
class WalletRepositoryTest {

    @Autowired
    private WalletRepository underTest;

    @Test
    void we_can_save_a_wallet() {
        Wallet wallet = new Wallet();
        wallet.setBalance(BigDecimal.valueOf(50));

        underTest.save(wallet);

        Optional<Wallet> found = underTest.findById(wallet.getId());
        assertTrue(found.isPresent());
        assertEquals(BigDecimal.valueOf(50), found.get().getBalance());
    }

    @Test
    void we_can_read() {
        Optional<Wallet> found = underTest.findById(UUID.fromString("8a72b4b2-3ec3-4a4a-891b-c1e4cbd6220d"));

        assertTrue(found.isPresent());
        assertThat(found.get().getBalance()).isEqualByComparingTo("100.00");
    }
}