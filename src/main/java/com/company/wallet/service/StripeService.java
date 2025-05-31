package com.company.wallet.service;

import com.company.wallet.config.StripeSimulatorProperties;
import com.company.wallet.dto.Payment;
import com.company.wallet.exception.StripeAmountTooSmallException;
import com.company.wallet.exception.StripeServiceException;
import com.company.wallet.handler.StripeRestTemplateResponseErrorHandler;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.net.URI;


/**
 * Handles the communication with Stripe.
 * <p>
 * A real implementation would call to String using their API/SDK.
 * This dummy implementation throws an error when trying to charge less than 10€.
 */
@Service
public class StripeService {

    private final StripeSimulatorProperties stripeProps;
    private final RestTemplate restTemplate;

    public StripeService(StripeSimulatorProperties stripeProps,
                         RestTemplateBuilder restTemplateBuilder) {
        this.stripeProps = stripeProps;
        this.restTemplate = restTemplateBuilder
                .errorHandler(new StripeRestTemplateResponseErrorHandler())
                .build();
    }

    /**
     * Charges money in the credit card.
     * <p>
     * Ignore the fact that no CVC or expiration date are provided.
     *
     * @param creditCardNumber The number of the credit card
     * @param amount           The amount that will be charged.
     * @throws StripeServiceException
     */
    public Payment charge(@NonNull String creditCardNumber, @NonNull BigDecimal amount) throws StripeServiceException {
        // based on the description above i add this check to avoid unnecessary calls to Stripe
        // it's defensive, but it can easily be removed if not needed
        if (amount.compareTo(BigDecimal.TEN) < 0) {
            throw new StripeAmountTooSmallException();
        }

        ChargeRequest body = new ChargeRequest(creditCardNumber, amount);
        URI uri = URI.create(stripeProps.getChargeUri());
        return restTemplate.postForObject(uri, body, Payment.class);
    }

    /**
     * Refunds the specified payment.
     */
    public void refund(@NonNull String paymentId) throws StripeServiceException {
        // Object.class because we don't read the body here.
        URI uri = URI.create(stripeProps.getRefundUri(paymentId));
        restTemplate.postForEntity(uri, null, Object.class);
    }

    @AllArgsConstructor
    private static class ChargeRequest {

        @NonNull
        @JsonProperty("credit_card")
        String creditCardNumber;

        @NonNull
        @JsonProperty("amount")
        BigDecimal amount;
    }
}