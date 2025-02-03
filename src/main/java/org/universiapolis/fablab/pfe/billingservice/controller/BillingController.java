package org.universiapolis.fablab.pfe.billingservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.universiapolis.fablab.pfe.billingservice.dto.request.InvoiceRequest;
import org.universiapolis.fablab.pfe.billingservice.dto.response.InvoiceResponse;
import org.universiapolis.fablab.pfe.billingservice.service.BillingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/billing")
@RefreshScope
@Tag(name = "Billing Service", description = "APIs for managing invoices and payments")
public class BillingController {

    private final BillingService billingService;

    // Constructor to inject BillingService
    public BillingController(BillingService billingService) {
        this.billingService = billingService;
    }

    @PostMapping("/invoices")
    @Operation(
            summary = "Create an invoice",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Invoice created"),
                    @ApiResponse(responseCode = "404", description = "Patient not found")
            }
    )
    public ResponseEntity<InvoiceResponse> createInvoice(@RequestBody InvoiceRequest request) {
        return ResponseEntity.ok(billingService.createInvoice(request));
    }

    @GetMapping("/invoices/patient/{patientId}")
    @Operation(summary = "Get invoices by patient ID")
    public ResponseEntity<List<InvoiceResponse>> getInvoicesByPatient(@PathVariable Long patientId) {
        return ResponseEntity.ok(billingService.getInvoicesByPatient(patientId));
    }
    @GetMapping("/all")
    @Operation(summary = "Get all invoices")
    public ResponseEntity<List<InvoiceResponse>> getAllInvoices() {
        List<InvoiceResponse> invoices = billingService.getAllInvoices();
        return ResponseEntity.ok(invoices);
    }


    @PostMapping("/invoices/{id}/pay")
    @Operation(summary = "Pay an invoice")
    public ResponseEntity<InvoiceResponse> payInvoice(
            @PathVariable Long id,
            @RequestParam BigDecimal amount,
            @RequestParam String paymentMethod
    ) {
        return ResponseEntity.ok(billingService.payInvoice(id, amount, paymentMethod));
    }
}