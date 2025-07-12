package com.tanuj.EcommerceApp.service.impl;

import com.tanuj.EcommerceApp.dto.ProductDto;
import com.tanuj.EcommerceApp.dto.Response;
import com.tanuj.EcommerceApp.entity.Category;
import com.tanuj.EcommerceApp.entity.Product;
import com.tanuj.EcommerceApp.exception.NotFoundException;
import com.tanuj.EcommerceApp.mapper.EntityDtoMapper;
import com.tanuj.EcommerceApp.repository.CategoryRepo;
import com.tanuj.EcommerceApp.repository.ProductRepo;
import com.tanuj.EcommerceApp.service.AwsS3Service;
import com.tanuj.EcommerceApp.service.interf.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepo productRepo;
    private final CategoryRepo categoryRepo;
    private final EntityDtoMapper entityDtoMapper;
    private final AwsS3Service awsS3Service;

    @Override
    public Response createProduct(Long categoryId, MultipartFile image, String name, String description, BigDecimal price) {
        Category category = categoryRepo.findById(categoryId).orElseThrow(()-> new NotFoundException("Category not found"));
        String productImageUrl = awsS3Service.saveImageToS3(image);

        Product product = new Product();
        product.setCategory(category);
        product.setPrice(price);
        product.setDescription(description);
        product.setImageUrl(productImageUrl);
        product.setName(name);

        productRepo.save(product);

        return Response.builder()
                .status(200)
                .message("Product added successfully")
                .build();
    }

    @Override
    public Response updateProduct(Long productId, Long categoryId, MultipartFile image, String name, String description, BigDecimal price) {
        Product product = productRepo.findById(productId).orElseThrow(()-> new NotFoundException("Product not found"));
        Category category = null;
        String productImageUrl = null;

        if (categoryId != null){
            category = categoryRepo.findById(categoryId).orElseThrow(()-> new NotFoundException("Category not found"));
        }
        if (image != null && !image.isEmpty()){
            productImageUrl = awsS3Service.saveImageToS3(image);
        }

        if (category != null) product.setCategory(category);
        if (name != null) product.setName(name);
        if (price != null) product.setPrice(price);
        if (description != null) product.setDescription(description);
        if (productImageUrl != null) product.setImageUrl(productImageUrl);

        productRepo.save(product);
        return Response.builder()
                .status(200)
                .message("Product updated successfully")
                .build();
    }

    @Override
    public Response deleteProduct(Long productId) {
        Product product = productRepo.findById(productId).orElseThrow(()-> new NotFoundException("Product not found"));
        productRepo.delete(product);
        return Response.builder()
                .status(200)
                .message("Product deleted successfully")
                .build();
    }

    @Override
    public Response getProductById(Long productId) {
        Product product = productRepo.findById(productId).orElseThrow(()-> new NotFoundException("Product not found"));
        ProductDto productDto = entityDtoMapper.mapProductToDtoBasic(product);
        return Response.builder()
                .status(200)
                .product(productDto)
                .build();
    }

    @Override
    public Response getAllProduct() {
        List<ProductDto> productList = productRepo.findAll(Sort.by(Sort.Direction.DESC, "id"))
                .stream()
                .map(entityDtoMapper::mapProductToDtoBasic)
                .toList();
        return Response.builder()
                .status(200)
                .productList(productList)
                .build();
    }

    @Override
    public Response getProductsByCategory(Long categoryId) {
        List<Product> products = productRepo.findByCategoryId(categoryId);
        if(products.isEmpty()){
            throw new NotFoundException("No products found for this category");
        }
        List<ProductDto> productDtoList = products.stream()
                .map(entityDtoMapper::mapProductToDtoBasic)
                .toList();

        return Response.builder()
                .status(200)
                .productList(productDtoList)
                .build();
    }

    @Override
    public Response searchProduct(String searchValue) {
        List<Product> products = productRepo.findByNameContainingOrDescriptionContaining(searchValue,searchValue);

        if(products.isEmpty()){
            throw new NotFoundException("No products found");
        }

        List<ProductDto> productDtoList = products.stream()
                .map(entityDtoMapper::mapProductToDtoBasic)
                .toList();

        return Response.builder()
                .status(200)
                .productList(productDtoList)
                .build();
    }
}
