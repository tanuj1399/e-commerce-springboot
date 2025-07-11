package com.tanuj.EcommerceApp.service.interf;

import com.tanuj.EcommerceApp.dto.CategoryDto;
import com.tanuj.EcommerceApp.dto.Response;

public interface CategoryService {

    Response createCategory(CategoryDto categoryDto);
    Response updateCategory(Long categoryId, CategoryDto categoryDto);
    Response getAllCategories();
    Response getCategoryById(Long categoryId);
    Response deleteCategory(Long categoryId);


}
