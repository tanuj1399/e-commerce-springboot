package com.tanuj.EcommerceApp.repository;

import com.tanuj.EcommerceApp.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentRepo extends JpaRepository<Payment, Long> {

    Optional<Payment> findByRazorpayOrderId(String razorpayId);
}
