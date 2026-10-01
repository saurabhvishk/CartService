package com.example.cartservice.clients;

import com.example.cartservice.dtos.ValidateTokenRequestDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Component
public class AuthClient {

    private final RestClient restClient;

    public AuthClient(RestClient.Builder builder, @Value("${services.auth.url}") String authServiceUrl) {
        this.restClient = builder.baseUrl(authServiceUrl).build();
    }

    // asks User Auth Service whether this token is valid for this user
    public boolean isValid(String token, Long userId) {
        try {
            Boolean valid = restClient.post()
                    .uri("/auth/validateToken")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new ValidateTokenRequestDto(token, userId))
                    .retrieve()
                    .body(Boolean.class);
            return Boolean.TRUE.equals(valid);
        } catch (RestClientException e) {
            // 401 from Auth, or Auth not reachable: treat as not valid
            return false;
        }
    }
}
