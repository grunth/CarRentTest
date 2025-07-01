package com.example.demo_car_rent.dto;

import com.example.demo_car_rent.model.CarType;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class ReservationResponse {
    private UUID id;
    private CarType carType;
    private LocalDateTime startTime;
    private int numberOfDays;
}
