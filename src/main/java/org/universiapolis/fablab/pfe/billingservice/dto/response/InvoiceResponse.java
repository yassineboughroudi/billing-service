package org.universiapolis.fablab.pfe.billingservice.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class InvoiceResponse {
    @Schema(description = "Invoice ID", example = "1")
    private Long id;

    @Schema(description = "Patient ID", example = "123")
    private Long patientId;

    @Schema(description = "Service name", example = "MRI Scan")
    private String serviceName;

    @Schema(description = "Amount", example = "500.00")
    private BigDecimal amount;

    @Schema(description = "Status", example = "PAID")
    private String status;
}