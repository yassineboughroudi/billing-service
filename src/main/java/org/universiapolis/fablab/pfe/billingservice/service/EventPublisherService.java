package org.universiapolis.fablab.pfe.billingservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EventPublisherService {

    private final RabbitTemplate rabbitTemplate;

    public void publishInvoicePaidEvent(Long invoiceId) {
        rabbitTemplate.convertAndSend(
                "invoice.events",
                "invoice.paid",
                "Invoice paid: " + invoiceId
        );
    }
}