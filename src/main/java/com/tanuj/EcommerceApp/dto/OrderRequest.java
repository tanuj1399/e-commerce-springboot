package com.tanuj.EcommerceApp.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.tanuj.EcommerceApp.entity.Payment;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class OrderRequest {

    private BigDecimal totalPrice;
    private List<OrderItemRequest> orderItems;
    private Payment paymentInfo;
}
