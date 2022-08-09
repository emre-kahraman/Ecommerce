package com.example.cartservice.repository;

import com.example.cartservice.entity.Cart;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface CartRepository extends CrudRepository<Cart, String> {

    Optional<Cart> getCartByUserId(String userId);
}
