package com.tanda.daraja_api.repository;

import com.tanda.daraja_api.entity.PendingPayment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PendingPaymentRepository extends JpaRepository<PendingPayment, Long> {
}
