package com.example.demo_car_rent.controller;

import com.example.demo_car_rent.dto.ReservationRequest;
import com.example.demo_car_rent.dto.ReservationResponse;
import com.example.demo_car_rent.model.CarType;
import com.example.demo_car_rent.service.ReservationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;

@WebFluxTest(controllers = ReservationController.class)
class ReservationControllerTest {
    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private ReservationService service;

    private ReservationRequest request;
    private ReservationResponse response;

    @BeforeEach
    void setup() {
        request = new ReservationRequest();
        request.setType(CarType.SEDAN);
        request.setDate(LocalDateTime.of(2025, 1, 1, 10, 0));
        request.setDays(3);

        response = new ReservationResponse();
        response.setId(UUID.randomUUID());
        response.setCarType(CarType.SEDAN);
        response.setStartTime(request.getDate());
        response.setNumberOfDays(request.getDays());
    }

    @Test
    void reserveCar_shouldReturnResponse() {
        Mockito.when(service.reserveCar(any())).thenReturn(Mono.just(response));

        webTestClient.post()
                .uri("/api/reservations")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(response.getId().toString())
                .jsonPath("$.carType").isEqualTo("SEDAN")
                .jsonPath("$.numberOfDays").isEqualTo(3);
    }

    @Test
    void getAllReservations_shouldReturnList() {
        Mockito.when(service.getAll()).thenReturn(Flux.just(response));

        webTestClient.get()
                .uri("/api/reservations")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.length()").isEqualTo(1)
                .jsonPath("$[0].id").isEqualTo(response.getId().toString())
                .jsonPath("$[0].carType").isEqualTo("SEDAN")
                .jsonPath("$[0].numberOfDays").isEqualTo(3);
    }
}
