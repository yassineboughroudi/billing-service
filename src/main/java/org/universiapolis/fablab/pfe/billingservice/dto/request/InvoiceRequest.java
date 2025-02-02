package org.universiapolis.fablab.pfe.billingservice.dto.request;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

@Data
public class InvoiceRequest {
    @NotNull(message = "Patient ID is required")
    private Long patientId;

    @NotBlank(message = "Service name is required")
    private String serviceName;

    @Positive(message = "Amount must be positive")
    private BigDecimal amount;
}