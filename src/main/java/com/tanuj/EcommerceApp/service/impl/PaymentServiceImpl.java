package com.tanuj.EcommerceApp.service.impl;

import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.tanuj.EcommerceApp.dto.Razorpay;
import com.tanuj.EcommerceApp.entity.Payment;
import com.tanuj.EcommerceApp.enums.PaymentMethod;
import com.tanuj.EcommerceApp.enums.PaymentStatus;
import com.tanuj.EcommerceApp.exception.NotFoundException;
import com.tanuj.EcommerceApp.repository.OrderRepo;
import com.tanuj.EcommerceApp.repository.PaymentRepo;
import com.tanuj.EcommerceApp.service.interf.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Hex;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    @Value("${razorpay.api.key}")
    private String razorpayKey;

    @Value("${razorpay.api.secret}")
    private String razorpaySecret;

    private final PaymentRepo paymentRepo;
    private final OrderRepo orderRepo;

    @Override
    public Razorpay initiatePayment(Razorpay razorpayRequest) {

        try {
            RazorpayClient razorpayClient = new RazorpayClient(razorpayKey, razorpaySecret);

            JSONObject razorpayOrderReq = new JSONObject();
            razorpayOrderReq.put("amount", razorpayRequest.getAmount().multiply(BigDecimal.valueOf(100))); //convert from paise to rupees
            razorpayOrderReq.put("currency", "INR");
//            razorpayOrderReq.put("receiptId", "txn_" + UUID.randomUUID());
            razorpayOrderReq.put("payment_capture", 1);

            Order razorpayOrder = razorpayClient.orders.create(razorpayOrderReq);
            Payment payment = new Payment();
            payment.setOrder(orderRepo.findById(razorpayRequest.getOrderId()).orElseThrow(() -> new NotFoundException("Order id not found!")));
            payment.setRazorpayOrderId(razorpayOrder.get("id"));
            payment.setStatus(PaymentStatus.INITIATED.name());
            payment.setMethod(PaymentMethod.UPI.name());
            paymentRepo.save(payment);

            return Razorpay.builder()
                    .razorpayOrderId(razorpayOrder.get("id"))
                    .orderId(razorpayRequest.getOrderId())
                    .amount(razorpayRequest.getAmount())
                    .method(PaymentMethod.UPI.name())
                    .status(PaymentStatus.INITIATED.name())
                    .build();
        } catch (RazorpayException e) {
            e.printStackTrace();
            throw new RuntimeException("Unable to complete payment!" + e.getMessage());
        }
    }

    @Override
    public Razorpay confirmPayment(Razorpay razorpayRequest) {

        try {

            String payload = razorpayRequest.getRazorpayOrderId() + "|" + razorpayRequest.getRazorpayPaymentId();

            Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKey = new SecretKeySpec(razorpaySecret.getBytes(), "HmacSHA256");
            sha256_HMAC.init(secretKey);

            byte[] hash = sha256_HMAC.doFinal(payload.getBytes(StandardCharsets.UTF_8));
            String generatedSignature = Hex.encodeHexString(hash);
            log.info(generatedSignature);
            boolean isValid = generatedSignature.equals(razorpayRequest.getRazorpaySignature());

            Optional<Payment> optionalPayment = paymentRepo.findByRazorpayOrderId(razorpayRequest.getRazorpayOrderId());

            if (optionalPayment.isEmpty()) {
                throw new NotFoundException("Razorpay order not found!");
            }
            Payment payment = optionalPayment.get();
            payment.setRazorpayPaymentId(razorpayRequest.getRazorpayPaymentId());
            payment.setRazorpaySignature(razorpayRequest.getRazorpaySignature());


            if (!isValid) {
                payment.setStatus(PaymentStatus.FAILED.name());
                paymentRepo.save(payment);

                return Razorpay.builder()
                        .paymentSuccess(PaymentStatus.FAILED.name())
                        .razorpayPaymentId(razorpayRequest.getRazorpayPaymentId())
                        .razorpaySignature(razorpayRequest.getRazorpaySignature())
                        .razorpayOrderId(razorpayRequest.getRazorpayOrderId())
                        .orderId(razorpayRequest.getOrderId())
                        .method(PaymentMethod.UPI.name())
                        .amount(razorpayRequest.getAmount())
                        .build();
            }
            else {
                payment.setStatus(PaymentStatus.SUCCESS.name());
                paymentRepo.save(payment);

                return Razorpay.builder()
                        .paymentSuccess(PaymentStatus.SUCCESS.name())
                        .razorpayPaymentId(razorpayRequest.getRazorpayPaymentId())
                        .razorpaySignature(razorpayRequest.getRazorpaySignature())
                        .razorpayOrderId(razorpayRequest.getRazorpayOrderId())
                        .orderId(razorpayRequest.getOrderId())
                        .method(PaymentMethod.UPI.name())
                        .amount(razorpayRequest.getAmount())
                        .build();
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Unable to verify payment!" + e.getMessage());
        }
    }
}


