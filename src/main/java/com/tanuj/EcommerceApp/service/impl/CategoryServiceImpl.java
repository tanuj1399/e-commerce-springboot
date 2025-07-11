package com.tanuj.EcommerceApp.service.impl;

import com.tanuj.EcommerceApp.dto.CategoryDto;
import com.tanuj.EcommerceApp.dto.Response;
import com.tanuj.EcommerceApp.entity.Category;
import com.tanuj.EcommerceApp.exception.NotFoundException;
import com.tanuj.EcommerceApp.mapper.EntityDtoMapper;
import com.tanuj.EcommerceApp.repository.CategoryRepo;
import com.tanuj.EcommerceApp.service.interf.CategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepo categoryRepo;
    private final EntityDtoMapper entityDtoMapper;


    @Override
    public Response createCategory(CategoryDto categoryRequest) {

        Category category = new Category();
        category.setName(categoryRequest.getName());
        categoryRepo.save(category);

        return Response.builder()
                .status(200)
                .message("Category created successfully")
                .build();
    }

    @Override
    public Response updateCategory(Long categoryId, CategoryDto categoryDto) {

        Category category = categoryRepo.findById(categoryId).orElseThrow(()-> new NotFoundException("Category not found"));
        category.setName(categoryDto.getName());
        categoryRepo.save(category);

        return Response.builder()
                .status(200)
                .message("Category updated successfully")
                .build();
    }

    @Override
    public Response getAllCategories() {
        List<Category> categories = categoryRepo.findAll();
        List<CategoryDto> categoryDtoList = categories.stream()
                .map(entityDtoMapper::mapCategoryToDtoBasic)
                .toList();
        return Response.builder()
                .status(200)
                .categoryList(categoryDtoList)
                .build();
    }

    @Override
    public Response getCategoryById(Long categoryId) {

        Category category = categoryRepo.findById(categoryId).orElseThrow(()-> new NotFoundException("Category not found"));
        CategoryDto categoryDto = entityDtoMapper.mapCategoryToDtoBasic(category);

        return Response.builder()
                .status(200)
                .category(categoryDto)
                .build();
    }

    @Override
    public Response deleteCategory(Long categoryId) {
        Category category = categoryRepo.findById(categoryId).orElseThrow(()-> new NotFoundException("Category not found"));
        categoryRepo.delete(category);

        return Response.builder()
                .status(200)
                .message("Category deleted successfully")
                .build();
    }
}
