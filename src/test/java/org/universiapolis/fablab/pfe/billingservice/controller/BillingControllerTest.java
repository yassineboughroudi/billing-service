package org.universiapolis.fablab.pfe.billingservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.universiapolis.fablab.pfe.billingservice.dto.request.InvoiceRequest;
import org.universiapolis.fablab.pfe.billingservice.dto.response.InvoiceResponse;
import org.universiapolis.fablab.pfe.billingservice.service.BillingService;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class BillingControllerTest {

    private MockMvc mockMvc;

    @Mock
    private BillingService billingService;

    @InjectMocks
    private BillingController billingController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(billingController).build();
    }

    @Test
    void testCreateInvoice() throws Exception {
        // Given
        InvoiceRequest request = new InvoiceRequest();
        request.setPatientId(1L);
        request.setServiceName("MRI Scan");
        request.setAmount(BigDecimal.valueOf(500.0));

        InvoiceResponse response = InvoiceResponse.builder()
                .id(1L)
                .patientId(1L)
                .serviceName("MRI Scan")
                .amount(BigDecimal.valueOf(500.0))
                .status("PENDING")
                .build();

        // Mock the service layer
        when(billingService.createInvoice(any(InvoiceRequest.class))).thenReturn(response);

        // When + Then (verify JSON response)
        mockMvc.perform(post("/billing/invoices")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.serviceName").value("MRI Scan"))
                .andExpect(jsonPath("$.amount").value(500.0))
                .andExpect(jsonPath("$.status").value("PENDING"));

        verify(billingService, times(1)).createInvoice(any(InvoiceRequest.class));
    }
}
