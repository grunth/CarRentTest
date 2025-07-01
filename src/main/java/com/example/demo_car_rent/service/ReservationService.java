package com.example.demo_car_rent.service;

import com.example.demo_car_rent.dto.ReservationRequest;
import com.example.demo_car_rent.dto.ReservationResponse;
import com.example.demo_car_rent.model.Reservation;
import com.example.demo_car_rent.repository.CarInventoryRepository;
import com.example.demo_car_rent.repository.ReservationRepository;
import com.example.demo_car_rent.repository.ReservationTemplateRepository;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@AllArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final ReservationTemplateRepository reservationTemplateRepository;
    private final CarInventoryRepository inventoryRepository;
    private final ModelMapper mapper;

    public Mono<ReservationResponse> reserveCar(ReservationRequest request) {
        return inventoryRepository.findByCarType(request.getType().name())
                .flatMap(inventory -> {
                    if (inventory.getAvailableCount() <= 0) {
                        return Mono.error(new RuntimeException("No " + request.getType() + " cars available"));
                    }

                    inventory.setAvailableCount(inventory.getAvailableCount() - 1);

                    return inventoryRepository.save(inventory)
                            .then(Mono.defer(() -> {
                                Reservation reservation = Reservation.builder()
                                        .id(request.getId() != null ? request.getId() : null)
                                        .carType(request.getType())
                                        .startTime(request.getDate())
                                        .numberOfDays(request.getDays())
                                        .build();
                                return reservationTemplateRepository.upsertReservation(reservation);
                            }));
                })
                .map(saved -> mapper.map(saved, ReservationResponse.class));
    }

    public Flux<ReservationResponse> getAll() {
        return reservationRepository.findAll()
                .map(reservation -> mapper.map(reservation, ReservationResponse.class));
    }
}
