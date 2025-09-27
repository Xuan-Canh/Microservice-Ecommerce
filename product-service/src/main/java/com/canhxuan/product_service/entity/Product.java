package com.canhxuan.product_service.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "products")
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    @JsonBackReference
    Category category;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference
    List<ProductAttribute> attributes;


    @Column(name = "product_name", nullable = false)
    String name;

    @Column(name = "product_description")
    String description;

    @Column(name = "product_price", nullable = false)
    double price = 0.0;

    @Column(name = "quantity", nullable = false)
    int quantity;

    @Column(name = "sku", nullable = false, unique = true)
    String sku;

    @Column(name = "status", nullable = false)
    String status = "ACTIVE";

    LocalDateTime created_at;
    LocalDateTime updated_at;
}
