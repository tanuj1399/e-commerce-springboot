package com.tanuj.EcommerceApp.controller;

import com.razorpay.RazorpayException;
import com.tanuj.EcommerceApp.dto.OrderRequest;
import com.tanuj.EcommerceApp.dto.PaymentDto;
import com.tanuj.EcommerceApp.dto.Response;
import com.tanuj.EcommerceApp.enums.OrderStatus;
import com.tanuj.EcommerceApp.service.PaymentService;
import com.tanuj.EcommerceApp.service.interf.OrderItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/order")
@RequiredArgsConstructor
public class OrderItemController {

    private final OrderItemService orderItemService;
    private final PaymentService paymentService;

    @PostMapping("/create-order")
    public ResponseEntity<PaymentDto> placeOrder(@RequestBody OrderRequest orderRequest) throws RazorpayException {
//        PaymentReqResp paymentReqResp = paymentService.initiatePayment(orderRequest.getTotalPrice(), "INR");
//        return ResponseEntity.ok(orderItemService.placeOrder(orderRequest));
        return ResponseEntity.ok(paymentService.initiatePayment(orderRequest.getOrderId(), orderRequest.getTotalPrice(), "INR"));
    }

    @PostMapping("/confirm-order")
    public ResponseEntity<?> confirmOrder(@RequestBody OrderRequest orderRequest,
                                                 @RequestParam String razorpayOrderId,
                                                 @RequestParam String razorpayPaymentId,
                                                 @RequestParam String razorpaySignature){

        if (!paymentService.confirmPayment(razorpayOrderId, razorpayPaymentId, razorpaySignature))
            return ResponseEntity.badRequest().body("Payment failed");

        return ResponseEntity.ok(orderItemService.placeOrder(orderRequest));

    }

    @PutMapping("/update-order-status/{orderId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Response> updateOrderItemStatus(@PathVariable Long orderItemId, @RequestParam String status){
        return ResponseEntity.ok(orderItemService.updateOrderItemStatus(orderItemId, status));
    }

    @GetMapping("/filter")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Response> filterOrderItems(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)LocalDateTime endDate,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long itemId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "1000") int size
    ){
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
        OrderStatus orderStatus = status != null ? OrderStatus.valueOf(status.toUpperCase()) : null;

        return ResponseEntity.ok(orderItemService.filterOrderItems(orderStatus, startDate, endDate, itemId, pageable));
    }

}
