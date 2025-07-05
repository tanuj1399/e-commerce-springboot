package com.tanuj.EcommerceApp.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.tanuj.EcommerceApp.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {

    private Long id;

    private String name;
    private String password;
    private String email;

    private String phoneNumber;
    private UserRole role;

    private List<OrderItemDto> orderItemList;

    private AddressDto address;
    private LocalDateTime createdAt;
}
