package com.example.demo_car_rent.repository;

import com.example.demo_car_rent.model.CarInventory;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Mono;

public interface CarInventoryRepository extends R2dbcRepository<CarInventory, String> {
    Mono<CarInventory> findByCarType(String carType);
}
