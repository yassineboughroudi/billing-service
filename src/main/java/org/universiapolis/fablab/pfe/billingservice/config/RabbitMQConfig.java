package org.universiapolis.fablab.pfe.billingservice.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    // Exchange for patient events
    @Bean
    public TopicExchange patientExchange() {
        return new TopicExchange("patient.events");
    }

    // Queue for patient.created events
    @Bean
    public Queue patientCreatedQueue() {
        return new Queue("billing.patient.created");
    }

    // Bind queue to exchange
    @Bean
    public Binding patientCreatedBinding() {
        return BindingBuilder.bind(patientCreatedQueue())
                .to(patientExchange())
                .with("patient.created");
    }

    // Exchange for invoice events
    @Bean
    public TopicExchange invoiceExchange() {
        return new TopicExchange("invoice.events");
    }
}