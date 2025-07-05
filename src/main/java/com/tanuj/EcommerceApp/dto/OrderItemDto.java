package com.tanuj.EcommerceApp.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.tanuj.EcommerceApp.entity.Order;
import com.tanuj.EcommerceApp.entity.Product;
import com.tanuj.EcommerceApp.entity.User;
import com.tanuj.EcommerceApp.enums.OrderStatus;
import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@AllArgsConstructor
@NoArgsConstructor
public class OrderItemDto {

    private Long id;
    private int quantity;
    private BigDecimal price;
    private OrderStatus status;
    private UserDto user;
    private ProductDto product;
    private OrderDto order;
    private LocalDateTime createdAt;
}
