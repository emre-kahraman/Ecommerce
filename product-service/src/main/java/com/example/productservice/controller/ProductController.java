package com.example.productservice.controller;

import com.example.productservice.dto.AddItemToCartRequest;
import com.example.productservice.dto.ProductDTO;
import com.example.productservice.dto.SaveProductRequest;
import com.example.productservice.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public ResponseEntity<List<ProductDTO>> getProducts(){
        return productService.getProducts();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDTO> getProduct(@PathVariable String id){
        return productService.getProduct(id);
    }

    @PostMapping
    public ResponseEntity<ProductDTO> saveProduct(@RequestBody SaveProductRequest saveProductRequest){
        return productService.saveProduct(saveProductRequest);
    }

    @PostMapping("/addItemToCart")
    public ResponseEntity<HttpStatus> addItemToCart(@RequestBody AddItemToCartRequest addItemToCartRequest){
        return productService.addItemToCart(addItemToCartRequest);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteProduct(@PathVariable String id){
        return productService.deleteProduct(id);
    }
}
