package com.example.demo_car_rent.controller;

import com.example.demo_car_rent.dto.ReservationRequest;
import com.example.demo_car_rent.dto.ReservationResponse;
import com.example.demo_car_rent.service.ExternalApiService;
import com.example.demo_car_rent.service.ReservationService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/reservations")
@AllArgsConstructor
public class ReservationController {

    private final ReservationService service;
    private final ExternalApiService externalApiService;

    @PostMapping
    public Mono<ReservationResponse> reserve(@RequestBody ReservationRequest request) {
        return service.reserveCar(request);
    }

    @GetMapping
    public Flux<ReservationResponse> all() {
        return service.getAll();
    }

    @PostMapping("/external")
    public Mono<String> callExternal() {
        return externalApiService.callExternalApi();
    }
}
