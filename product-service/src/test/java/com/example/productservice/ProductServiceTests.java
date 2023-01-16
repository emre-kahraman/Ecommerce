package com.example.productservice;

import com.example.productservice.dto.AddItemToCartRequest;
import com.example.productservice.dto.ProductDTO;
import com.example.productservice.dto.SaveProductRequest;
import com.example.productservice.entity.Product;
import com.example.productservice.repository.ProductRepository;
import com.example.productservice.service.ProductService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTests {

    @InjectMocks
    ProductService productService;

    @Mock
    ProductRepository productRepository;

    @Mock
    KafkaTemplate<String, AddItemToCartRequest> kafkaTemplate;

    @Test
    public void itShouldGetAllProducts(){
        Product product = Product.builder().name("test").category("test").price(BigDecimal.valueOf(1)).build();
        Product product2 = Product.builder().name("test2").category("test2").price(BigDecimal.valueOf(1)).build();
        List<Product> productList = List.of(product, product2);

        when(productRepository.findAll()).thenReturn(productList);

        ResponseEntity<List<ProductDTO>> responseEntity = productService.getProducts();

        assertEquals(responseEntity.getBody().size(), productList.size());
        assertEquals(responseEntity.getStatusCode(), HttpStatus.OK);

    }

    @Test
    public void itShouldGetProductById(){
        Product product = Product.builder().id("1").name("test").category("test").price(BigDecimal.valueOf(1)).build();

        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));

        ResponseEntity<ProductDTO> responseEntity = productService.getProduct(product.getId());

        assertEquals(responseEntity.getBody().getName(), product.getName());
        assertEquals(responseEntity.getStatusCode(), HttpStatus.OK);

    }

    @Test
    public void itShouldGetProductsByCategory(){
        Product product = Product.builder().name("test").category("test").price(BigDecimal.valueOf(1)).build();
        Product product2 = Product.builder().name("test2").category("test").price(BigDecimal.valueOf(1)).build();
        List<Product> productList = List.of(product, product2);

        when(productRepository.getProductsByCategory(product.getCategory())).thenReturn(Optional.of(productList));

        ResponseEntity<List<ProductDTO>> responseEntity = productService.getProductsByCategory(product.getCategory());

        assertEquals(responseEntity.getBody().size(), productList.size());
        assertEquals(responseEntity.getStatusCode(), HttpStatus.OK);

    }

    @Test
    public void itShouldSaveProduct(){
        SaveProductRequest saveProductRequest = SaveProductRequest.builder()
                .name("test").category("test").price(BigDecimal.valueOf(1)).build();
        Product product = Product.builder().name("test").category("test").price(BigDecimal.valueOf(1)).build();

        when(productRepository.save(any())).thenReturn(product);

        ResponseEntity<ProductDTO> responseEntity = productService.saveProduct(saveProductRequest);

        assertEquals(responseEntity.getBody().getName(), product.getName());
        assertEquals(responseEntity.getStatusCode(), HttpStatus.CREATED);

    }

    @Test
    public void itShouldDeleteProduct(){
        Product product = Product.builder().id("1").name("test").category("test").price(BigDecimal.valueOf(1)).build();

        when(productRepository.existsById(product.getId())).thenReturn(true);
        doNothing().when(productRepository).deleteById(product.getId());

        ResponseEntity<HttpStatus> responseEntity = productService.deleteProduct(product.getId());

        assertEquals(responseEntity.getStatusCode(), HttpStatus.OK);

    }

    @Test
    public void itShouldAddItemToCart(){
        AddItemToCartRequest addItemToCartRequest = new AddItemToCartRequest("1", "1", "test", BigDecimal.valueOf(1), 1);

        when(productRepository.existsById(addItemToCartRequest.getProductId())).thenReturn(true);
        when(kafkaTemplate.send(eq("products"), any(), any())).thenReturn(null);

        ResponseEntity<HttpStatus> responseEntity = productService.addItemToCart(addItemToCartRequest);

        assertEquals(responseEntity.getStatusCode(), HttpStatus.OK);

    }
}
