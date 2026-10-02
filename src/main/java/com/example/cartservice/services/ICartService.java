package com.example.cartservice.services;

import com.example.cartservice.dtos.CartDto;
import com.example.cartservice.exception.ProductNotFoundException;

public interface ICartService {
    CartDto addItem(Long userId, Long productId, int quantity) throws ProductNotFoundException;
    CartDto getCart(Long userId);
}
