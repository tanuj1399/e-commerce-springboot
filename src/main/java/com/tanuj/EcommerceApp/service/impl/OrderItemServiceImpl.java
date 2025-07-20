package com.tanuj.EcommerceApp.service.impl;

import com.tanuj.EcommerceApp.dto.OrderItemDto;
import com.tanuj.EcommerceApp.dto.OrderRequest;
import com.tanuj.EcommerceApp.dto.Response;
import com.tanuj.EcommerceApp.entity.Order;
import com.tanuj.EcommerceApp.entity.OrderItem;
import com.tanuj.EcommerceApp.entity.Product;
import com.tanuj.EcommerceApp.entity.User;
import com.tanuj.EcommerceApp.enums.OrderStatus;
import com.tanuj.EcommerceApp.exception.NotFoundException;
import com.tanuj.EcommerceApp.mapper.EntityDtoMapper;
import com.tanuj.EcommerceApp.repository.OrderItemRepo;
import com.tanuj.EcommerceApp.repository.OrderRepo;
import com.tanuj.EcommerceApp.repository.ProductRepo;
import com.tanuj.EcommerceApp.service.interf.OrderItemService;
import com.tanuj.EcommerceApp.service.interf.UserService;
import com.tanuj.EcommerceApp.specification.OrderItemSpecification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderItemServiceImpl implements OrderItemService {

    private final OrderRepo orderRepo;
    private final OrderItemRepo orderItemRepo;
    private final ProductRepo productRepo;
    private final UserService userService;
    private final EntityDtoMapper entityDtoMapper;


    @Override
    public Response placeOrder(OrderRequest orderRequest) {

        User user = userService.getLoginUser();

        //map order request items to order entities
        List<OrderItem> orderItems = orderRequest.getOrderItems().stream().map(orderItemRequest -> {
            Product product = productRepo.findById(orderItemRequest.getProductId())
                    .orElseThrow(()-> new NotFoundException("Product not found"));
            OrderItem orderItem = new OrderItem();
            orderItem.setProduct(product);
            orderItem.setQuantity(orderItemRequest.getQuantity());
            orderItem.setPrice(product.getPrice().multiply(BigDecimal.valueOf(orderItemRequest.getQuantity()))); //set price according to quantity
            orderItem.setStatus(OrderStatus.PENDING);
            orderItem.setUser(user);
            return orderItem;
        }).toList();

        //calculate total price
        BigDecimal totalPrice = orderRequest.getTotalPrice() != null && orderRequest.getTotalPrice().compareTo(BigDecimal.ZERO) > 0
                ? orderRequest.getTotalPrice()
                : orderItems.stream().map(OrderItem::getPrice).reduce(BigDecimal.ZERO, BigDecimal::add);

        Order order = new Order();
        order.setOrderItemList(orderItems);
        order.setTotalPrice(totalPrice);

        //set the order reference in each order item
        orderItems.forEach(orderItem -> orderItem.setOrder(order));

        orderRepo.save(order);

        return Response.builder()
                .status(200)
                .message("Order placed, confirm payment")
                .build();
    }

    @Override
    public Response updateOrderItemStatus(Long orderItemId, String status) {
        OrderItem orderItem = orderItemRepo.findById(orderItemId).orElseThrow(()-> new NotFoundException("Order item not found"));

        orderItem.setStatus(OrderStatus.valueOf(status.toUpperCase()));
        orderItemRepo.save(orderItem);

        return Response.builder()
                .status(200)
                .message("Order status updated successfully")
                .build();
    }

    @Override
    public Response filterOrderItems(OrderStatus status, LocalDateTime startDate, LocalDateTime endDate, Long itemId, Pageable pageable) {

        Specification<OrderItem> spec = Specification.where(OrderItemSpecification.hasStatus(status))
                .and(OrderItemSpecification.createdBetween(startDate,endDate))
                .and(OrderItemSpecification.hasItemId(itemId));

        Page<OrderItem> orderItemPage = orderItemRepo.findAll(spec, pageable);

        if (orderItemPage.isEmpty()){
            throw new NotFoundException("Order not found");
        }
        List<OrderItemDto> orderItemDtoList = orderItemPage.getContent().stream()
                .map(entityDtoMapper::mapOrderItemToDtoPlusProductAndUser)
                .toList();
        return Response.builder()
                .status(200)
                .orderItemList(orderItemDtoList)
                .totalPage(orderItemPage.getTotalPages())
                .totalElement(orderItemPage.getTotalElements())
                .build();
    }
}
