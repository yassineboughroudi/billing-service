package org.universiapolis.fablab.pfe.billingservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.universiapolis.fablab.pfe.billingservice.dto.request.InvoiceRequest;
import org.universiapolis.fablab.pfe.billingservice.dto.response.InvoiceResponse;
import org.universiapolis.fablab.pfe.billingservice.exception.InvalidPaymentException;
import org.universiapolis.fablab.pfe.billingservice.exception.PatientNotFoundException;
import org.universiapolis.fablab.pfe.billingservice.exception.PatientServiceUnavailableException;
import org.universiapolis.fablab.pfe.billingservice.model.Invoice;
import org.universiapolis.fablab.pfe.billingservice.model.InvoiceStatus;
import org.universiapolis.fablab.pfe.billingservice.model.Payment;
import org.universiapolis.fablab.pfe.billingservice.model.PaymentStatus;
import org.universiapolis.fablab.pfe.billingservice.repository.InvoiceRepository;
import org.universiapolis.fablab.pfe.billingservice.repository.PaymentRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BillingService {

    private final InvoiceRepository invoiceRepository;
    private final PaymentRepository paymentRepository;
    private final RestTemplate restTemplate;
    private final EventPublisherService eventPublisher;

    private void validatePatient(Long patientId) {
        String patientServiceUrl = "http://patient-management-service:8080/api/patients/" + patientId;
        try {
            ResponseEntity<Void> response = restTemplate.getForEntity(patientServiceUrl, Void.class);
            if (response.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new PatientNotFoundException("Patient not found: " + patientId);
            }
        } catch (HttpClientErrorException.NotFound e) {
            throw new PatientNotFoundException("Patient not found: " + patientId);
        } catch (Exception e) {
            throw new PatientServiceUnavailableException("Patient service is unavailable");
        }
    }



    // Create an invoice using InvoiceRequest DTO
    @Transactional
    public InvoiceResponse createInvoice(InvoiceRequest request) {
        validatePatient(request.getPatientId());

        Invoice invoice = Invoice.builder()
                .patientId(request.getPatientId())
                .serviceName(request.getServiceName())
                .amount(request.getAmount())
                .build();

        Invoice savedInvoice = invoiceRepository.save(invoice);
        return convertToInvoiceResponse(savedInvoice);
    }

    // Process payment and return InvoiceResponse
    @Transactional
    public InvoiceResponse payInvoice(Long invoiceId, BigDecimal amount, String paymentMethod) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new IllegalArgumentException("Invoice not found"));

        if (!invoice.getStatus().equals(InvoiceStatus.PENDING)) {
            throw new InvalidPaymentException("Invoice is not payable");
        }

        if (invoice.getAmount().compareTo(amount) != 0) {
            throw new InvalidPaymentException("Payment amount does not match invoice amount");
        }

        if (!List.of("CREDIT_CARD", "INSURANCE", "CASH").contains(paymentMethod)) {
            throw new InvalidPaymentException("Invalid payment method: " + paymentMethod);
        }

        Payment payment = Payment.builder()
                .invoice(invoice)
                .amount(amount)
                .paymentMethod(paymentMethod)
                .status(PaymentStatus.COMPLETED)
                .build();
        paymentRepository.save(payment);

        invoice.setStatus(InvoiceStatus.PAID);
        Invoice updatedInvoice = invoiceRepository.save(invoice);

        // Publish event (optional)
        eventPublisher.publishInvoicePaidEvent(invoiceId);

        return convertToInvoiceResponse(updatedInvoice);
    }

    // Get invoices for a patient and convert to DTOs
    public List<InvoiceResponse> getInvoicesByPatient(Long patientId) {
        List<Invoice> invoices = invoiceRepository.findByPatientId(patientId);
        return invoices.stream()
                .map(this::convertToInvoiceResponse)
                .collect(Collectors.toList());
    }

    // Convert Invoice entity to InvoiceResponse DTO
    private InvoiceResponse convertToInvoiceResponse(Invoice invoice) {
        return InvoiceResponse.builder()
                .id(invoice.getId())
                .patientId(invoice.getPatientId())
                .serviceName(invoice.getServiceName())
                .amount(invoice.getAmount())
                .status(invoice.getStatus().toString())
                .build();
    }

    // Get all invoices and convert them to InvoiceResponse DTOs
    public List<InvoiceResponse> getAllInvoices() {
        List<Invoice> invoices = invoiceRepository.findAll();
        return invoices.stream()
                .map(this::convertToInvoiceResponse)
                .collect(Collectors.toList());
    }

}