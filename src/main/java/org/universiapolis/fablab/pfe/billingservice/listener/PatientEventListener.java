package org.universiapolis.fablab.pfe.billingservice.listener;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class PatientEventListener {

    @RabbitListener(queues = "billing.patient.created")
    public void handlePatientCreatedEvent(String patientId) {
        // Initialize billing profile for new patient
        System.out.println("New patient created: " + patientId);
    }
}