package com.example.demo_car_rent.model;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.UUID;

@Table(name = "reservations")
@Data
@Builder
public class Reservation {

    @Id
    private UUID id;

    private CarType carType;

    private LocalDateTime startTime;

    private int numberOfDays;
}
