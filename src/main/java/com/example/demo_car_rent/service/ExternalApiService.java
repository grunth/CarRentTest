package com.example.demo_car_rent.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ExternalApiService {

    private final WebClient webClient;

    @Value("${custom.external-api.token-uri}")
    private String tokenUri;

    @Value("${custom.external-api.client-id}")
    private String clientId;

    @Value("${custom.external-api.client-secret}")
    private String clientSecret;

    @Value("${custom.external-api.url}")
    private String externalApiUrl;

    public Mono<String> callExternalApi() {
        return getToken().flatMap(token ->
                webClient.post()
                        .uri(externalApiUrl)
                        .header("Authorization", "Bearer " + token)
                        .bodyValue("{ \"message\": \"Hello from other realm!\" }")
                        .retrieve()
                        .bodyToMono(String.class)
        );
    }

    private Mono<String> getToken() {
        return webClient.post()
                .uri(tokenUri)
                .header("Content-Type", "application/x-www-form-urlencoded")
                .body(BodyInserters
                        .fromFormData("grant_type", "password")
                        .with("client_id", clientId)
                        .with("client_secret", clientSecret)
                        .with("username", "testuser2")
                        .with("password", "testuser2"))
                .retrieve()
                .bodyToMono(TokenResponse.class)
                .map(TokenResponse::getAccessToken);
    }

    private record TokenResponse(String access_token) {
        public String getAccessToken() {
            return access_token;
        }
    }
}
