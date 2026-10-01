package com.example.cartservice.dtos;

import lombok.Getter;
import lombok.Setter;

// only the fields the cart needs from Product Catalog's response
@Getter
@Setter
public class ProductDto {
    private Long id;
    private String name;
    private Double price;
}
