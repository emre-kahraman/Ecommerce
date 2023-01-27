package com.example.productservice.service;

import com.example.productservice.dto.*;
import com.example.productservice.entity.Product;
import com.example.productservice.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final KafkaTemplate<String, AddItemToCartRequest> productTemplate;
    private final KafkaTemplate<String, UpdateCartItemRequest> updatedProductTemplate;
    private final KafkaTemplate<String, DeleteCartItemRequest> deletedProductTemplate;
    public ResponseEntity<List<ProductDTO>> getProducts() {
        List<Product> productList = productRepository.findAll();
        List<ProductDTO> productDTOList = new ArrayList<>();
        productList.forEach(product -> productDTOList.add(convert(product)));
        return new ResponseEntity<>(productDTOList, HttpStatus.OK);
    }

    public ResponseEntity<ProductDTO> getProduct(String id) {
        Optional<Product> product = productRepository.findById(id);
        if(product.isEmpty())
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        ProductDTO productDTO = convert(product.get());
        return new ResponseEntity<>(productDTO, HttpStatus.OK);
    }

    public ResponseEntity<List<ProductDTO>> getProductsByCategory(String category) {
        Optional<List<Product>> productList = productRepository.getProductsByCategory(category);
        if(productList.isEmpty())
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        List<ProductDTO> productDTOList = new ArrayList<>();
        productList.get().forEach(product -> productDTOList.add(convert(product)));
        return new ResponseEntity<>(productDTOList, HttpStatus.OK);
    }

    public ResponseEntity<ProductDTO> saveProduct(SaveProductRequest saveProductRequest) {
        Product product = Product.builder().name(saveProductRequest.getName()).category(saveProductRequest.getCategory())
                .category(saveProductRequest.getCategory()).price(saveProductRequest.getPrice()).build();
        Product savedProduct = productRepository.save(product);
        ProductDTO productDTO = convert(savedProduct);
        System.out.println(savedProduct.getId());
        return new ResponseEntity<>(productDTO, HttpStatus.CREATED);
    }

    public ResponseEntity<ProductDTO> updateProduct(String productId, SaveProductRequest saveProductRequest) {
        Optional<Product> product = productRepository.findById(productId);
        if(product.isEmpty())
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        product.get().setName(saveProductRequest.getName());
        product.get().setCategory(saveProductRequest.getCategory());
        product.get().setPrice(saveProductRequest.getPrice());
        Product savedProduct = productRepository.save(product.get());
        updatedProductTemplate.send("updatedProducts", savedProduct.getId(),
                new UpdateCartItemRequest(savedProduct.getId(), savedProduct.getName(), savedProduct.getPrice()));
        ProductDTO productDTO = convert(savedProduct);
        return new ResponseEntity<>(productDTO, HttpStatus.OK);
    }

    public ResponseEntity<HttpStatus> deleteProduct(String id) {
        if(!productRepository.existsById(id))
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        productRepository.deleteById(id);
        deletedProductTemplate.send("deletedProducts", id, new DeleteCartItemRequest(id));
        return new ResponseEntity<>(HttpStatus.OK);
    }

    public ProductDTO convert(Product product){
        return ProductDTO.builder().name(product.getName())
                .category(product.getCategory()).price(product.getPrice()).build();
    }

    public ResponseEntity<HttpStatus> addItemToCart(AddItemToCartRequest addItemToCartRequest) {
        if(!productRepository.existsById(addItemToCartRequest.getProductId()))
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        productTemplate.send("products", addItemToCartRequest.getUserId(), addItemToCartRequest);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
