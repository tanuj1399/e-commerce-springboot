package com.tanuj.EcommerceApp.repository;

import com.tanuj.EcommerceApp.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepo extends JpaRepository<Category, Long> {

}
