package com.example.demo_car_rent;

import com.example.demo_car_rent.dto.ReservationRequest;
import com.example.demo_car_rent.model.CarType;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ReservationIntegrationTest {

    @Container
    public static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("car_rental")
            .withUsername("postgres")
            .withPassword("password");

    @LocalServerPort
    private int port;

    private WebTestClient webTestClient;

    @Autowired
    private DatabaseClient databaseClient;  // Риктивный клиент для SQL

    @DynamicPropertySource
    static void setDatasourceProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.r2dbc.url", () ->
                "r2dbc:postgresql://" + postgres.getHost() + ":" + postgres.getFirstMappedPort() + "/" + postgres.getDatabaseName());
        registry.add("spring.r2dbc.username", postgres::getUsername);
        registry.add("spring.r2dbc.password", postgres::getPassword);
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @BeforeEach
    public void setup() {
        this.webTestClient = WebTestClient.bindToServer()
                .baseUrl("http://localhost:" + port + "/api/reservations")
                .build();

        // Инициализация базы — создание таблиц и вставка данных
        databaseClient.sql("DROP TABLE IF EXISTS reservations").then()
                .then(databaseClient.sql("DROP TABLE IF EXISTS car_inventory").then())
                .then(databaseClient.sql(
                        "CREATE TABLE reservations (" +
                                "id UUID PRIMARY KEY, " +
                                "car_type VARCHAR NOT NULL, " +
                                "start_time TIMESTAMP NOT NULL, " +
                                "number_of_days INTEGER NOT NULL)").then())
                .then(databaseClient.sql(
                        "CREATE TABLE car_inventory (" +
                                "car_type VARCHAR PRIMARY KEY, " +
                                "available_count INT NOT NULL)").then())
                .then(databaseClient.sql(
                        "INSERT INTO car_inventory (car_type, available_count) VALUES " +
                                "('SEDAN', 3), ('SUV', 2), ('VAN', 1)").then())
                .block(); // ждем окончания, чтобы база была готова к тесту
    }

    @AfterEach
    public void cleanup() {
        // Очистка — удаляем таблицы, чтобы не было конфликтов в следующих тестах
        databaseClient.sql("DROP TABLE IF EXISTS reservations").then()
                .then(databaseClient.sql("DROP TABLE IF EXISTS car_inventory").then())
                .block();
    }

    @Test
    @Order(1)
    public void testReserveCarAndGetAll() {
        ReservationRequest request = new ReservationRequest();
        request.setType(CarType.SEDAN);
        request.setDate(LocalDateTime.now().plusDays(1));
        request.setDays(3);

        webTestClient.post()
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(request), ReservationRequest.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.carType").isEqualTo("SEDAN")
                .jsonPath("$.numberOfDays").isEqualTo(3);

        webTestClient.get()
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.length()").isEqualTo(1)
                .jsonPath("$[0].carType").isEqualTo("SEDAN");
    }
}
