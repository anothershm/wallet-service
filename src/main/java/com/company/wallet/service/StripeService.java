package com.company.wallet.service;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.company.wallet.config.StripeSimulatorProperties;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.net.URI;


/**
 * Handles the communication with Stripe.
 *
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
     *
     * Ignore the fact that no CVC or expiration date are provided.
     *
     * @param creditCardNumber The number of the credit card
     * @param amount The amount that will be charged.
     *
     * @throws StripeServiceException
     */
    public Payment charge(@NonNull String creditCardNumber, @NonNull BigDecimal amount) throws StripeServiceException {
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