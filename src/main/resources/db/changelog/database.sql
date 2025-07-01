CREATE TABLE reservations (
    id UUID PRIMARY KEY,
    car_type VARCHAR NOT NULL,
    start_time TIMESTAMP NOT NULL,
    number_of_days INTEGER NOT NULL
);

CREATE TABLE car_inventory (
    car_type VARCHAR PRIMARY KEY,
    available_count INT NOT NULL
);

INSERT INTO car_inventory (car_type, available_count) VALUES
('SEDAN', 3),
('SUV', 2),
('VAN', 1);