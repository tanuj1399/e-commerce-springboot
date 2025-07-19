package com.tanuj.EcommerceApp.service;

import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.tanuj.EcommerceApp.dto.PaymentDto;
import com.tanuj.EcommerceApp.entity.Payment;
import com.tanuj.EcommerceApp.enums.PaymentMethod;
import com.tanuj.EcommerceApp.enums.PaymentStatus;
import com.tanuj.EcommerceApp.exception.NotFoundException;
import com.tanuj.EcommerceApp.repository.OrderRepo;
import com.tanuj.EcommerceApp.repository.PaymentRepo;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentService {

    @Value("${razorpay.api.key}")
    private String razorpayKey;

    @Value("${razorpay.api.secret}")
    private String razorpaySecret;

    private final PaymentRepo paymentRepo;
    private final OrderRepo orderRepo;

    public PaymentDto initiatePayment(Long orderId, BigDecimal amount, String currency) {

        try {
            RazorpayClient razorpayClient = new RazorpayClient(razorpayKey, razorpaySecret);

            JSONObject razorpayOrderReq = new JSONObject();
            razorpayOrderReq.put("amount", amount.multiply(BigDecimal.valueOf(100))); //convert from paise to rupees
            razorpayOrderReq.put("currency", currency);
            razorpayOrderReq.put("receiptId", "txn_" + UUID.randomUUID());
            razorpayOrderReq.put("payment_capture", 1);

            Order razorpayOrder = razorpayClient.orders.create(razorpayOrderReq);
            Payment payment = new Payment();
            payment.setOrder(orderRepo.findById(orderId).orElseThrow(() -> new NotFoundException("Order id not found!")));
            payment.setRazorpayOrderId(razorpayOrder.get("id"));
            payment.setStatus(PaymentStatus.INITIATED.name());
            payment.setMethod(PaymentMethod.CARD.name());
            paymentRepo.save(payment);

            return PaymentDto.builder()
                    .amount(amount)
                    .razorpayOrderId(razorpayOrder.get("id"))
                    .status(String.valueOf(PaymentStatus.INITIATED))
                    .currency(razorpayOrder.get("currency"))
                    .build();
        } catch (RazorpayException e) {
            e.printStackTrace();
            throw new RuntimeException("Unable to complete payment!" + e.getMessage());
        }
    }

    public Boolean confirmPayment(String razorpayOrderId, String razorpayPaymentId, String razorpaySignature){

        try {

            String payload = razorpayOrderId + "|" + razorpayPaymentId;

            Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKey = new SecretKeySpec(razorpaySecret.getBytes(), "HmacSHA256");
            sha256_HMAC.init(secretKey);

            byte[] hash = sha256_HMAC.doFinal(payload.getBytes(StandardCharsets.UTF_8));
            String generatedSignature = Base64.getEncoder().encodeToString(hash);

            boolean isValid = generatedSignature.equals(razorpaySignature);

            Optional<Payment> optionalPayment = paymentRepo.findByRazorpayOrderId(razorpayOrderId);

            if (optionalPayment.isEmpty()){
                throw new NotFoundException("Razorpay order not found!");
            }
            Payment payment = optionalPayment.get();
            payment.setRazorpayPaymentId(razorpayPaymentId);
            payment.setRazorpaySignature(razorpaySignature);
            payment.setStatus(PaymentStatus.SUCCESS.name());

            paymentRepo.save(payment);

            return isValid;

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Unable to verify payment!" + e.getMessage());
        }
    }
}
