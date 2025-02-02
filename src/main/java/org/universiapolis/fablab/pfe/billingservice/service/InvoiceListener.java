/*
package org.universiapolis.fablab.pfe.billingservice.service;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
public class InvoiceListener {

    private final BillingService billingService;

    public InvoiceListener(BillingService billingService) {
        this.billingService = billingService;
    }

    @RabbitListener(queues = "detection_results")
    public void handleDetectionResult(String message) {
        // Parse the message
        //System.out.println("Received message: " + message);
        // Use the billingService to create an invoice
        // billingService.createInvoice(patientId, serviceName, amount);
    }
}
*/
