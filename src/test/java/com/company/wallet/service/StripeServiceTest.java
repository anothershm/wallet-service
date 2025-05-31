package com.company.wallet.service;

import com.company.wallet.config.StripeSimulatorProperties;
import com.company.wallet.dto.Payment;
import com.company.wallet.exception.StripeAmountTooSmallException;
import com.company.wallet.exception.StripeServiceException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.client.ExpectedCount.once;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

public class StripeServiceTest {

    private StripeService underTest;
    private MockRestServiceServer mockServer;


    @BeforeEach
    void setUp() {
        StripeSimulatorProperties props = new StripeSimulatorProperties();
        props.setBaseUri("http://localhost:9999");

        RestTemplate restTemplate = new RestTemplate();
        mockServer = MockRestServiceServer.createServer(restTemplate);

        RestTemplateBuilder builder = new RestTemplateBuilder()
                .requestFactory(restTemplate::getRequestFactory);

        underTest = new StripeService(props, builder);
    }


    @Test
    void whenTheAmountIsLessThan10ExpectException() {
        Assertions.assertThrows(StripeAmountTooSmallException.class, () -> {
            underTest.charge("4242 4242 4242 4242", new BigDecimal(5));
        });
        mockServer.verify();
    }

    @Test
    void whenChargeIsSuccessful() throws StripeServiceException {
        mockServer.expect(once(),
                        requestTo("http://localhost:9999/v1/stripe-simulator/charges"))
                .andRespond(withSuccess("{\"id\":\"payment_123\"}", MediaType.APPLICATION_JSON));

        Payment payment = underTest.charge("4242 4242 4242 4242", new BigDecimal("15"));
        assertEquals("payment_123", payment.getId());
        mockServer.verify();
    }

    @Test
    void whenTheresARefund() throws StripeServiceException {
        String paymentId = "123";
        mockServer.expect(once(),
                        requestTo("http://localhost:9999/v1/stripe-simulator/payments/" + paymentId + "/refunds"))
                .andRespond(withSuccess("{\"someObject\":\"example\"}", MediaType.APPLICATION_JSON));
         underTest.refund(paymentId);
        mockServer.verify();
    }
}

