package com.tanuj.EcommerceApp.repository;

import com.tanuj.EcommerceApp.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepo extends JpaRepository<Order, Long> {

}
