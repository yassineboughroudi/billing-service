package org.universiapolis.fablab.pfe.billingservice.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.universiapolis.fablab.pfe.billingservice.dto.request.InvoiceRequest;
import org.universiapolis.fablab.pfe.billingservice.dto.response.InvoiceResponse;
import org.universiapolis.fablab.pfe.billingservice.exception.PatientNotFoundException;
import org.universiapolis.fablab.pfe.billingservice.exception.PatientServiceUnavailableException;
import org.universiapolis.fablab.pfe.billingservice.model.Invoice;
import org.universiapolis.fablab.pfe.billingservice.model.InvoiceStatus;
import org.universiapolis.fablab.pfe.billingservice.repository.InvoiceRepository;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class BillingServiceTest {

    @Mock
    private InvoiceRepository invoiceRepository;

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private EventPublisherService eventPublisher;

    @InjectMocks
    private BillingService billingService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateInvoice_Success() {
        // Given
        InvoiceRequest request = new InvoiceRequest();
        request.setPatientId(1L);
        request.setServiceName("CT Scan");
        request.setAmount(BigDecimal.valueOf(200.0));

        // Simulate a 200 OK from Patient Service
        when(restTemplate.getForEntity(anyString(), eq(Void.class)))
                .thenReturn(ResponseEntity.ok().build());

        // Mock invoice save
        Invoice savedInvoice = Invoice.builder()
                .id(1L)
                .patientId(1L)
                .serviceName("CT Scan")
                .amount(BigDecimal.valueOf(200.0))
                .status(InvoiceStatus.PENDING)
                .build();

        when(invoiceRepository.save(any(Invoice.class))).thenReturn(savedInvoice);

        // When
        InvoiceResponse response = billingService.createInvoice(request);

        // Then
        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("CT Scan", response.getServiceName());
        assertEquals(BigDecimal.valueOf(200.0), response.getAmount());
        assertEquals("PENDING", response.getStatus());

        verify(invoiceRepository, times(1)).save(any(Invoice.class));
        verify(eventPublisher, never()).publishInvoicePaidEvent(anyLong());
    }

    @Test
    void testCreateInvoice_PatientNotFound() {
        // Given
        InvoiceRequest request = new InvoiceRequest();
        request.setPatientId(99L);
        request.setServiceName("MRI Scan");
        request.setAmount(BigDecimal.valueOf(500.0));

        // Ensure the EXACT "NotFound" subclass or .create(...) is used
        when(restTemplate.getForEntity(anyString(), eq(Void.class)))
                .thenThrow(HttpClientErrorException.create(
                        HttpStatus.NOT_FOUND,
                        "Not Found",
                        HttpHeaders.EMPTY,
                        null,
                        null
                ));

        // When + Then
        Exception exception = assertThrows(PatientNotFoundException.class, () -> {
            billingService.createInvoice(request);
        });

        assertEquals("Patient not found: 99", exception.getMessage());

        verify(restTemplate, times(1)).getForEntity(anyString(), eq(Void.class));
    }



    @Test
    void testCreateInvoice_PatientServiceUnavailable() {
        // Given
        InvoiceRequest request = new InvoiceRequest();
        request.setPatientId(99L);
        request.setServiceName("MRI Scan");
        request.setAmount(BigDecimal.valueOf(500.0));

        // Simulate a network failure (e.g., service down)
        when(restTemplate.getForEntity(anyString(), eq(Void.class)))
                .thenThrow(new ResourceAccessException("Connection refused"));

        // When + Then
        Exception exception = assertThrows(PatientServiceUnavailableException.class, () -> {
            billingService.createInvoice(request);
        });

        assertEquals("Patient service is unavailable", exception.getMessage());
    }
}
