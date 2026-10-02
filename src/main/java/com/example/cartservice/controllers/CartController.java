package com.example.cartservice.controllers;

import com.example.cartservice.dtos.AddItemRequestDto;
import com.example.cartservice.dtos.CartDto;
import com.example.cartservice.exception.ProductNotFoundException;
import com.example.cartservice.services.ICartService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/carts")
public class CartController {

    @Autowired
    private ICartService cartService;

    // PRD 3.1 Add to Cart
    @PostMapping("/me/items")
    public ResponseEntity<CartDto> addItem(Authentication authentication,
                                           @Valid @RequestBody AddItemRequestDto request) throws ProductNotFoundException {
        Long userId = (Long) authentication.getPrincipal();
        return ResponseEntity.ok(cartService.addItem(userId, request.getProductId(), request.getQuantity()));
    }

    // PRD 3.2 Cart Review
    @GetMapping("/me")
    public ResponseEntity<CartDto> getMyCart(Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        return ResponseEntity.ok(cartService.getCart(userId));
    }
}
