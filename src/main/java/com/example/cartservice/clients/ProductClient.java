package com.example.cartservice.clients;

import com.example.cartservice.dtos.ProductDto;
import com.example.cartservice.exception.ProductNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

@Component
public class ProductClient {

    private final RestClient restClient;

    public ProductClient(RestClient.Builder builder, @Value("${services.product.url}") String productServiceUrl) {
        this.restClient = builder.baseUrl(productServiceUrl).build();
    }

    public ProductDto getProduct(Long productId) throws ProductNotFoundException {
        try {
            ProductDto product = restClient.get()
                    .uri("/products/{id}", productId)
                    .retrieve()
                    .body(ProductDto.class);
            if (product == null || product.getId() == null) {
                throw new ProductNotFoundException("Product not found");
            }
            return product;
        } catch (HttpClientErrorException e) {
            // Product Catalog answers 4xx for unknown ids
            throw new ProductNotFoundException("Product not found");
        }
    }
}
