package com.company.wallet.service;

import com.company.wallet.config.StripeSimulatorProperties;
import com.company.wallet.service.StripeAmountTooSmallException;
import com.company.wallet.service.StripeService;
import com.company.wallet.service.StripeServiceException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertThrows;
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
    void test_exception() {
        Assertions.assertThrows(StripeAmountTooSmallException.class, () -> {
            underTest.charge("4242 4242 4242 4242", new BigDecimal(5));
        });
    }

    @Test
    void test_ok() throws StripeServiceException {
        mockServer.expect(once(),
                        requestTo("http://localhost:9999/v1/stripe-simulator/charges"))
                .andRespond(withSuccess("{\"id\":\"payment_123\"}", MediaType.APPLICATION_JSON));

        underTest.charge("4242 4242 4242 4242", new BigDecimal("15"));

        mockServer.verify();
    }
}

