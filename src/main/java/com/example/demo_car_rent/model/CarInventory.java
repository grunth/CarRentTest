package com.example.demo_car_rent.model;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("car_inventory")
@Data
@Builder
public class CarInventory {

    @Id
    private String carType;

    private int availableCount;
}
