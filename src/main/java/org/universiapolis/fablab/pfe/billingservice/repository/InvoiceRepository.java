package org.universiapolis.fablab.pfe.billingservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.universiapolis.fablab.pfe.billingservice.model.Invoice;
import java.util.List;

public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    List<Invoice> findByPatientId(Long patientId); // Fetch invoices by patient ID
}