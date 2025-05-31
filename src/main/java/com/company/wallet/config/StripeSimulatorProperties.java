package com.company.wallet.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "stripe.simulator")
public class StripeSimulatorProperties {

    private String baseUri;

    public String getChargeUri() {
        return baseUri + "/v1/stripe-simulator/charges";
    }

    public String getRefundUri(String paymentId) {
        return baseUri + "/v1/stripe-simulator/payments/" + paymentId + "/refunds";
    }

    public String getBaseUri() {
        return baseUri;
    }

    public void setBaseUri(String baseUri) {
        this.baseUri = baseUri;
    }
}
