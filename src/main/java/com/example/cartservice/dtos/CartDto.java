package com.example.cartservice.dtos;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class CartDto {
    private Long userId;
    private List<CartItemDto> items = new ArrayList<>();
    private Integer totalQuantity;
    private BigDecimal totalPrice;
}
