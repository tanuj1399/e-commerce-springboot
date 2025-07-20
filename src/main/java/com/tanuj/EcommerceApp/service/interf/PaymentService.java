package com.tanuj.EcommerceApp.service.interf;

import com.tanuj.EcommerceApp.dto.Razorpay;

public interface PaymentService {

    public Razorpay initiatePayment(Razorpay razorpayRequest);

    public Razorpay confirmPayment(Razorpay razorpayRequest);

}
