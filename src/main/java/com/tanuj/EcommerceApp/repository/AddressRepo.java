package com.tanuj.EcommerceApp.repository;

import com.tanuj.EcommerceApp.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AddressRepo extends JpaRepository<Address, Long> {

}
