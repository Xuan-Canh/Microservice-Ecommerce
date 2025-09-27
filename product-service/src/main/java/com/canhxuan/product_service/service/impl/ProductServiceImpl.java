package com.canhxuan.product_service.service.impl;

import com.canhxuan.product_service.dto.ProductAttributeDTO;
import com.canhxuan.product_service.dto.request.CreateProductRequest;
import com.canhxuan.product_service.dto.request.InventoryReservationRequest;
import com.canhxuan.product_service.dto.request.UpdateProductRequest;
import com.canhxuan.product_service.dto.response.InventoryReservationResponse;
import com.canhxuan.product_service.dto.response.ProductResponse;
import com.canhxuan.product_service.entity.Category;
import com.canhxuan.product_service.entity.Product;
import com.canhxuan.product_service.entity.ProductAttribute;
import com.canhxuan.product_service.entity.Reservation;
import com.canhxuan.product_service.repository.CategoryRepository;
import com.canhxuan.product_service.repository.ProductRepository;
import com.canhxuan.product_service.repository.ReservationRepository;
import com.canhxuan.product_service.service.ProductService;
import dto.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    private ProductResponse toDto(Product product) {
        if (product == null) {
            return null;
        }
        ProductResponse dto = new ProductResponse();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setDescription(product.getDescription());
        dto.setPrice(product.getPrice());
        dto.setQuantity(product.getQuantity());
        dto.setSku(product.getSku());
        dto.setStatus(product.getStatus());
        if (product.getAttributes() != null) {
            List<ProductAttributeDTO> attributesDto = product.getAttributes().stream()
                    .map(attr -> {
                        ProductAttributeDTO attrDto = new ProductAttributeDTO();
                        attrDto.setName(attr.getName());
                        attrDto.setValue(attr.getValue());
                        return attrDto;
                    })
                    .collect(Collectors.toList());
            dto.setAttributes(attributesDto);
        }
        dto.setCreatedAt(product.getCreated_at());
        dto.setUpdatedAt(product.getUpdated_at());
        return dto;
    }

    @Override
    public ApiResponse<ProductResponse> getById(Long id) {
        ApiResponse<ProductResponse> product = new ApiResponse<>();
        product.setStatus(200);
        product.setMessage("Get product successfully");
        product.setData(toDto(productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("prduct not found with id " + id))));
        return product;
    }

    @Override
    public ApiResponse<List<ProductResponse>> getAll() {
        ApiResponse<List<ProductResponse>> products = new ApiResponse<>();
        products.setStatus(200);
        products.setMessage("Get all products successfully");
        List<ProductResponse> productResponses = productRepository.findAll().stream()
                        .map(this::toDto)
                        .collect(Collectors.toList());
        products.setData(productResponses);
        return products;
    }

    @Override
    public ApiResponse<ProductResponse> create(CreateProductRequest request) {
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found with id " + request.getCategoryId()));
        Product product = new Product();
        product.setCategory(category);
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setQuantity(request.getQuantity());
        product.setSku(request.getSku());
        List<ProductAttribute> attributes = request.getAttributes().stream()
                        .map(dto -> {
                            ProductAttribute attribute = new ProductAttribute();
                            attribute.setName(dto.getName());
                            attribute.setValue(dto.getValue());
                            attribute.setProduct(product);
                            return attribute;
                        })
                                .collect(Collectors.toList());
        product.setAttributes(attributes);
        product.setCreated_at(LocalDateTime.now());
        ApiResponse<ProductResponse> response = new ApiResponse<>();
        response.setStatus(201);
        response.setMessage("Create product successfully");
        response.setData(toDto(productRepository.save(product)));
        return response;
    }

    @Override
    public ApiResponse<ProductResponse> update(Long id, UpdateProductRequest request) {
        Product product = productRepository.findById(id).orElseThrow(() -> new RuntimeException("Product not found with id " + id));
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found with id " + request.getCategoryId()));
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setQuantity(request.getQuantity());
        product.setSku(request.getSku());
        product.setCategory(category);
        product.getAttributes().clear();
        List<ProductAttribute> attributes = request.getAttributes().stream()
                        .map(dto -> {
                            ProductAttribute attribute = new ProductAttribute();
                            attribute.setName(dto.getName());
                            attribute.setValue(dto.getValue());
                            attribute.setProduct(product);
                            return attribute;
                        })
                                .collect(Collectors.toList());
        product.getAttributes().addAll(attributes);
        product.setAttributes(attributes);
        product.setUpdated_at(LocalDateTime.now());
        ApiResponse<ProductResponse> response = new ApiResponse<>();
        response.setStatus(201);
        response.setMessage("Update product successfully");
        response.setData(toDto(productRepository.save(product)));
        return response;
    }

    @Override
    public ApiResponse<?> delete(Long id) {
        ApiResponse<?> response = new ApiResponse<>();
        response.setStatus(200);
        response.setMessage("Delete product successfully");
        productRepository.deleteById(id);
        return response;
    }

    @Override
    public ApiResponse<InventoryReservationResponse> reserveInventory(InventoryReservationRequest request) {
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(()-> new RuntimeException("Product not found with id " + request.getProductId()));
        product.setQuantity(product.getQuantity() - request.getQuantity());
        Reservation reservation = new Reservation();
        reservation.setReservationId(request.getReservationId());
        reservation.setQuantity(request.getQuantity());
        reservation.setProductId(request.getProductId());
        reservation.setStatus("RESERVED");
        reservationRepository.save(reservation);
        productRepository.save(product);
        InventoryReservationResponse response = new InventoryReservationResponse(true);
        ApiResponse<InventoryReservationResponse> apiResponse = new ApiResponse<>();
        apiResponse.setStatus(200);
        apiResponse.setMessage("Reserve inventory successfully");
        apiResponse.setData(response);
        return apiResponse;
    }

    @Override
    public ApiResponse<?> releaseInventory(List<String> reservationIds) {
        if (reservationIds == null || reservationIds.isEmpty()) {
            throw new RuntimeException("ReservationId is required");
        }
        for (String reservationId : reservationIds) {
            Reservation reservation = reservationRepository.findById(reservationId)
                    .orElseThrow(() -> new RuntimeException("Reservation not found with id " + reservationId));
            if (reservation.getStatus().equals("RELEASED")) {
                throw new RuntimeException("Reservation already released with id " + reservationId);
            }

            Product product = productRepository.findById(reservation.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found with id " + reservation.getProductId()));
            product.setQuantity(product.getQuantity() + reservation.getQuantity());
            reservation.setStatus("RELEASED");
            productRepository.save(product);
        }
        ApiResponse<Void> apiResponse = new ApiResponse<>();
        apiResponse.setStatus(200);
        apiResponse.setMessage("Release inventory successfully");
        apiResponse.setData(null);
        return apiResponse;
    }


}
