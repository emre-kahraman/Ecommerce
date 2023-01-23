package com.example.productservice;

import com.example.productservice.config.KafkaConfig;
import com.example.productservice.dto.AddItemToCartRequest;
import com.example.productservice.dto.ProductDTO;
import com.example.productservice.dto.SaveProductRequest;
import com.example.productservice.entity.Product;
import com.example.productservice.repository.ProductRepository;
import com.example.productservice.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@MockBean({
        KafkaConfig.class
})
public class ProductIntegrationTests {

    @Autowired
    ProductService productService;

    @Autowired
    ProductRepository productRepository;

    @MockBean
    KafkaTemplate<String, AddItemToCartRequest> kafkaTemplate;

    @BeforeEach
    public void setup(){
        Product product = Product.builder().id("1")
                .name("test")
                .category("test")
                .price(BigDecimal.valueOf(1)).build();
        Product product2 = Product.builder().id("2")
                .name("test2")
                .category("test2")
                .price(BigDecimal.valueOf(1)).build();
        productRepository.deleteAll();
        productRepository.save(product);
        productRepository.save(product2);
    }

    @Test
    public void itShouldGetAllProducts(){

        ResponseEntity<List<ProductDTO>> responseEntity = productService.getProducts();

        assertEquals(responseEntity.getBody().size(), 2);
        assertEquals(responseEntity.getStatusCode(), HttpStatus.OK);

    }

    @Test
    public void itShouldGetProductById(){

        ResponseEntity<ProductDTO> responseEntity = productService.getProduct("1");

        assertEquals(responseEntity.getBody().getName(), "test");
        assertEquals(responseEntity.getStatusCode(), HttpStatus.OK);

    }

    @Test
    public void itShouldGetProductsByCategory(){

        ResponseEntity<List<ProductDTO>> responseEntity = productService.getProductsByCategory("test");

        assertEquals(responseEntity.getBody().size(), 1);
        assertEquals(responseEntity.getStatusCode(), HttpStatus.OK);

    }

    @Test
    public void itShouldSaveProduct(){
        SaveProductRequest saveProductRequest = SaveProductRequest.builder()
                .name("test3").category("test3").price(BigDecimal.valueOf(1)).build();

        ResponseEntity<ProductDTO> responseEntity = productService.saveProduct(saveProductRequest);

        assertEquals(responseEntity.getBody().getName(), "test3");
        assertEquals(responseEntity.getStatusCode(), HttpStatus.CREATED);

    }

    @Test
    public void itShouldUpdateProduct(){
        SaveProductRequest saveProductRequest = SaveProductRequest.builder()
                .name("test3").category("test3").price(BigDecimal.valueOf(1)).build();

        ResponseEntity<ProductDTO> responseEntity = productService.updateProduct("1", saveProductRequest);

        assertEquals(responseEntity.getBody().getName(), "test3");
        assertEquals(responseEntity.getStatusCode(), HttpStatus.OK);

    }

    @Test
    public void itShouldDeleteProduct(){

        ResponseEntity<HttpStatus> responseEntity = productService.deleteProduct("1");

        assertEquals(responseEntity.getStatusCode(), HttpStatus.OK);
        assertEquals(productRepository.findById("1"), Optional.empty());

    }
}
