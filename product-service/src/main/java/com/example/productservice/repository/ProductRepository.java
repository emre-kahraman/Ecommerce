package com.example.productservice.repository;

import com.example.productservice.entity.Product;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends MongoRepository<Product, String> {

    Optional<List<Product>> getProductsByCategory(String category);
}
