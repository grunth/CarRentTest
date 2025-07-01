package com.example.demo_car_rent.service;

import com.example.demo_car_rent.dto.ReservationRequest;
import com.example.demo_car_rent.dto.ReservationResponse;
import com.example.demo_car_rent.model.CarInventory;
import com.example.demo_car_rent.model.CarType;
import com.example.demo_car_rent.model.Reservation;
import com.example.demo_car_rent.repository.CarInventoryRepository;
import com.example.demo_car_rent.repository.ReservationRepository;
import com.example.demo_car_rent.repository.ReservationTemplateRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ReservationServiceTest {

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private ReservationTemplateRepository reservationTemplateRepository;

    @Mock
    private CarInventoryRepository inventoryRepository;

    @Mock
    private ModelMapper mapper;

    @InjectMocks
    private ReservationService service;

    private ReservationRequest request;
    private CarInventory inventory;
    private Reservation savedReservation;
    private ReservationResponse expectedResponse;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        request = new ReservationRequest();
        request.setId(UUID.randomUUID());
        request.setType(CarType.SEDAN);
        request.setDate(LocalDateTime.of(2025, 1, 1, 10, 0));
        request.setDays(3);

        inventory = CarInventory
                .builder()
                .carType(CarType.SEDAN.toString())
                .availableCount(5)
                .build();

        savedReservation = Reservation.builder()
                .id(request.getId())
                .carType(CarType.SEDAN)
                .startTime(request.getDate())
                .numberOfDays(request.getDays())
                .build();

        expectedResponse = new ReservationResponse();
        expectedResponse.setId(savedReservation.getId());
        expectedResponse.setCarType(savedReservation.getCarType());
        expectedResponse.setStartTime(savedReservation.getStartTime());
        expectedResponse.setNumberOfDays(savedReservation.getNumberOfDays());
    }

    @Test
    void reserveCar_whenAvailable_shouldReturnResponse() {
        when(inventoryRepository.findByCarType("SEDAN")).thenReturn(Mono.just(inventory));
        when(inventoryRepository.save(any())).thenReturn(Mono.just(inventory));
        when(reservationTemplateRepository.upsertReservation(any())).thenReturn(Mono.just(savedReservation));
        when(mapper.map(savedReservation, ReservationResponse.class)).thenReturn(expectedResponse);

        StepVerifier.create(service.reserveCar(request))
                .expectNext(expectedResponse)
                .verifyComplete();

        verify(inventoryRepository).findByCarType("SEDAN");
        verify(inventoryRepository).save(any(CarInventory.class));
        verify(reservationTemplateRepository).upsertReservation(any());
        verify(mapper).map(savedReservation, ReservationResponse.class);
    }

    @Test
    void reserveCar_whenNoAvailable_shouldReturnError() {
        inventory.setAvailableCount(0);
        when(inventoryRepository.findByCarType("SEDAN")).thenReturn(Mono.just(inventory));

        StepVerifier.create(service.reserveCar(request))
                .expectErrorMatches(throwable ->
                        throwable instanceof RuntimeException &&
                                throwable.getMessage().contains("No SEDAN cars available"))
                .verify();

        verify(inventoryRepository).findByCarType("SEDAN");
        verify(inventoryRepository, never()).save(any());
        verify(reservationTemplateRepository, never()).upsertReservation(any());
    }

    @Test
    void getAll_shouldReturnMappedResponses() {
        Reservation reservation = savedReservation;
        when(reservationRepository.findAll()).thenReturn(Flux.just(reservation));
        when(mapper.map(reservation, ReservationResponse.class)).thenReturn(expectedResponse);

        StepVerifier.create(service.getAll())
                .expectNext(expectedResponse)
                .verifyComplete();

        verify(reservationRepository).findAll();
        verify(mapper).map(reservation, ReservationResponse.class);
    }
}