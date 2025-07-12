package com.tanuj.EcommerceApp.service.interf;

import com.tanuj.EcommerceApp.dto.OrderRequest;
import com.tanuj.EcommerceApp.dto.Response;
import com.tanuj.EcommerceApp.enums.OrderStatus;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;

public interface OrderItemService {

    Response placeOrder(OrderRequest orderRequest);
    Response updateOrderItemStatus(Long orderItemId, String status);
    Response filterOrderItems(OrderStatus status, LocalDateTime startDate, LocalDateTime endDate, Long itemId, Pageable pageable);
}
