package com.example.demo_car_rent.repository;

import com.example.demo_car_rent.model.Reservation;
import lombok.AllArgsConstructor;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Query;
import org.springframework.data.relational.core.query.Update;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
@AllArgsConstructor
public class ReservationTemplateRepository {

    private final R2dbcEntityTemplate template;

    public Mono<Reservation> upsertReservation(Reservation reservation) {
        if (reservation.getId() == null) {
            reservation.setId(UUID.randomUUID());
        }

        Query query = Query.query(Criteria.where("id").is(reservation.getId()));

        return template.exists(query, Reservation.class)
                .flatMap(exists -> {
                    if (exists) {
                        Update update = Update.update("car_type", reservation.getCarType())
                                .set("start_time", reservation.getStartTime())
                                .set("number_of_days", reservation.getNumberOfDays());
                        return template.update(query, update, Reservation.class)
                                .thenReturn(reservation);
                    } else {
                        return template.insert(reservation);
                    }
                });
    }
}

