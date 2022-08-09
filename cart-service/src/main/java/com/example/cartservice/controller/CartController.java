package com.example.cartservice.controller;

import com.example.cartservice.entity.Cart;
import com.example.cartservice.entity.CartItem;
import com.example.cartservice.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/carts")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @GetMapping("/{id}")
    public ResponseEntity<Cart> getCartById(@PathVariable String id){
        return cartService.getCartById(id);
    }

    @GetMapping("/getCartByUserId/{userId}")
    public ResponseEntity<Cart> getCartByUserId(@PathVariable String userId){
        return cartService.getCartByUserId(userId);
    }

    @DeleteMapping("/{userId}/removeCartItem/{productId}")
    public ResponseEntity<Cart> removeCartItem(@PathVariable("userId") String userId, @RequestBody CartItem cartItem){
        return cartService.removeCartItem(userId, cartItem);
    }

}
