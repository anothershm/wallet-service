package com.company.wallet.handler;

import com.company.wallet.exception.StripeAmountTooSmallException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class StripeRestTemplateResponseErrorHandlerTest {

    private final StripeRestTemplateResponseErrorHandler underTest = new StripeRestTemplateResponseErrorHandler();

    @Test
    void whenResponseIs422HandleErrorThrowsException() throws IOException {
        ClientHttpResponse mockResponse = mock(ClientHttpResponse.class);
        when(mockResponse.getStatusCode()).thenReturn(HttpStatus.UNPROCESSABLE_ENTITY);

        assertThrows(StripeAmountTooSmallException.class, () -> {
            underTest.handleError(mockResponse);
        });
    }

    @Test
    void hasErrorWhenResponse4xxOr5xxIsTrue() throws IOException {
        ClientHttpResponse mock4xx = mock(ClientHttpResponse.class);
        when(mock4xx.getStatusCode()).thenReturn(HttpStatus.BAD_REQUEST);

        ClientHttpResponse mock5xx = mock(ClientHttpResponse.class);
        when(mock5xx.getStatusCode()).thenReturn(HttpStatus.INTERNAL_SERVER_ERROR);

        assertTrue(underTest.hasError(mock4xx));
        assertTrue(underTest.hasError(mock5xx));
    }

    @Test
    void hasErrorWhenResponse200IsFalse() throws IOException {
        ClientHttpResponse mock200 = mock(ClientHttpResponse.class);
        when(mock200.getStatusCode()).thenReturn(HttpStatus.OK);

        assertFalse(underTest.hasError(mock200));
    }
}
