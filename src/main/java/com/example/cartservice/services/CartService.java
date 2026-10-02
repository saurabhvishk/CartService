package com.example.cartservice.services;

import com.example.cartservice.clients.ProductClient;
import com.example.cartservice.config.CacheConfig;
import com.example.cartservice.dtos.CartDto;
import com.example.cartservice.dtos.CartItemDto;
import com.example.cartservice.dtos.ProductDto;
import com.example.cartservice.exception.ProductNotFoundException;
import com.example.cartservice.models.Cart;
import com.example.cartservice.models.CartItem;
import com.example.cartservice.repos.CartRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.Optional;

@Service
public class CartService implements ICartService {

    @Autowired
    private CartRepo cartRepo;

    @Autowired
    private ProductClient productClient;

    // after a successful add, the returned cart replaces the one in Redis
    @Override
    @CachePut(value = CacheConfig.CARTS_CACHE, key = "#userId")
    public CartDto addItem(Long userId, Long productId, int quantity) throws ProductNotFoundException {
        // 1. the product must exist; we also need its name and price
        ProductDto product = productClient.getProduct(productId);

        // 2. this user's cart, or a new empty one
        Cart cart = cartRepo.findByUserId(userId).orElseGet(() -> {
            Cart newCart = new Cart();
            newCart.setUserId(userId);
            return newCart;
        });

        // 3. already in the cart? add to the quantity, otherwise add a new line
        Optional<CartItem> existing = cart.getItems().stream()
                .filter(item -> item.getProductId().equals(productId))
                .findFirst();
        if (existing.isPresent()) {
            existing.get().setQuantity(existing.get().getQuantity() + quantity);
        } else {
            CartItem item = new CartItem();
            item.setProductId(product.getId());
            item.setName(product.getName());
            item.setPrice(product.getPrice());
            item.setQuantity(quantity);
            cart.getItems().add(item);
        }

        cart.setUpdatedAt(Instant.now());
        return toDto(cartRepo.save(cart));
    }

    // first look in Redis; only on a miss read MongoDB and store the result in Redis
    @Override
    @Cacheable(value = CacheConfig.CARTS_CACHE, key = "#userId")
    public CartDto getCart(Long userId) {
        return cartRepo.findByUserId(userId)
                .map(this::toDto)
                .orElseGet(() -> emptyCart(userId));
    }

    private CartDto toDto(Cart cart) {
        CartDto cartDto = emptyCart(cart.getUserId());
        BigDecimal totalPrice = BigDecimal.ZERO;
        int totalQuantity = 0;

        for (CartItem item : cart.getItems()) {
            BigDecimal lineTotal = BigDecimal.valueOf(item.getPrice())
                    .multiply(BigDecimal.valueOf(item.getQuantity()))
                    .setScale(2, RoundingMode.HALF_UP);

            CartItemDto itemDto = new CartItemDto();
            itemDto.setProductId(item.getProductId());
            itemDto.setName(item.getName());
            itemDto.setPrice(item.getPrice());
            itemDto.setQuantity(item.getQuantity());
            itemDto.setLineTotal(lineTotal);
            cartDto.getItems().add(itemDto);

            totalPrice = totalPrice.add(lineTotal);
            totalQuantity += item.getQuantity();
        }

        cartDto.setTotalQuantity(totalQuantity);
        cartDto.setTotalPrice(totalPrice.setScale(2, RoundingMode.HALF_UP));
        return cartDto;
    }

    private CartDto emptyCart(Long userId) {
        CartDto cartDto = new CartDto();
        cartDto.setUserId(userId);
        cartDto.setTotalQuantity(0);
        cartDto.setTotalPrice(BigDecimal.ZERO.setScale(2));
        return cartDto;
    }
}
