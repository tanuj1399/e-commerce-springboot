package com.tanuj.EcommerceApp.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Response {

    private int status;
    private String message;

    @Builder.Default
    private final LocalDateTime timestamp = LocalDateTime.now();

    private String token;
    private String role;
    private String expirationTime;

    private int totalPage;
    private Long totalElement;

    private AddressDto address;

    private UserDto user;
    private List<UserDto> userList;

    private CategoryDto category;
    private List<CategoryDto> categoryList;

    private OrderDto order;
    private List<OrderDto> orderList;

    private OrderItemDto orderItem;
    private List<OrderItemDto> orderItemList;

    private ProductDto product;
    private List<ProductDto> productList;

}
