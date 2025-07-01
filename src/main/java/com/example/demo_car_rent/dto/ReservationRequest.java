package com.example.demo_car_rent.dto;

import com.example.demo_car_rent.model.CarType;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class ReservationRequest {
    private UUID id;
    private CarType type;
    private LocalDateTime date;
    private int days;
}
