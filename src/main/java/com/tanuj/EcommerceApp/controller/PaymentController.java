package com.tanuj.EcommerceApp.controller;

import com.tanuj.EcommerceApp.dto.OrderRequest;
import com.tanuj.EcommerceApp.dto.Razorpay;
import com.tanuj.EcommerceApp.service.interf.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/initiate-payment")
    public ResponseEntity<Razorpay> initiatePayment(@RequestBody Razorpay razorpayRequest){

        return ResponseEntity.ok(paymentService.initiatePayment(razorpayRequest));
//        return ResponseEntity.ok(paymentService.initiatePayment(orderRequest.getOrderId(), orderRequest.getTotalPrice(), "INR"));
    }
    @PostMapping("/confirm-payment")
    public ResponseEntity<Razorpay> confirmOrder(@RequestBody Razorpay razorpayRequest){

            return ResponseEntity.ok(paymentService.confirmPayment(razorpayRequest));
    }
}
