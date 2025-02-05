package org.universiapolis.fablab.pfe.billingservice.service.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.universiapolis.fablab.pfe.billingservice.dto.request.InvoiceRequest;
import org.universiapolis.fablab.pfe.billingservice.dto.response.InvoiceResponse;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class BillingControllerIT {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void testCreateInvoiceIntegration() {
        // Given
        InvoiceRequest request = new InvoiceRequest();
        request.setPatientId(1L);
        request.setServiceName("X-Ray");
        request.setAmount(BigDecimal.valueOf(150.0));

        // When
        ResponseEntity<InvoiceResponse> response = restTemplate.postForEntity(
                "/billing/invoices",
                request,
                InvoiceResponse.class
        );

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("X-Ray", response.getBody().getServiceName());
        assertEquals(BigDecimal.valueOf(150.0), response.getBody().getAmount());
    }
}
