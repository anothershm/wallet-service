package com.company.wallet.controller;

import com.company.wallet.dto.TopUpRequest;
import com.company.wallet.model.Wallet;
import com.company.wallet.service.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/wallets")
@RequiredArgsConstructor
public class WalletController {

    private final WalletService walletService;

    @GetMapping("/{id}")
    public ResponseEntity<Wallet> getWallet(@PathVariable UUID id) {
        return ResponseEntity.ok(walletService.getWallet(id));
    }

    @PostMapping("/{id}/topup")
    public ResponseEntity<Wallet> topUp(
            @PathVariable UUID id,
            @RequestBody TopUpRequest request) {
        return ResponseEntity.ok(walletService.topUp(id, request.getAmount(), request.getCreditCardNumber()));
    }


}

