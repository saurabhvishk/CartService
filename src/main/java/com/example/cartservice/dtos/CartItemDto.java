package com.example.cartservice.dtos;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class CartItemDto {
    private Long productId;
    private String name;
    private Double price;
    private Integer quantity;
    private BigDecimal lineTotal;
}
