package com.example.demo_car_rent.repository;

import com.example.demo_car_rent.model.Reservation;
import org.springframework.data.r2dbc.repository.R2dbcRepository;

public interface ReservationRepository extends R2dbcRepository<Reservation, String> {
}
