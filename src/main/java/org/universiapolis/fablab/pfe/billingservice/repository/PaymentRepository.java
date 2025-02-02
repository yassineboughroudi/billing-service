package org.universiapolis.fablab.pfe.billingservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.universiapolis.fablab.pfe.billingservice.model.Payment;
import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByInvoiceId(Long invoiceId); // Fetch payments by invoice ID
}