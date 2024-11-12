package com.workshop.passenger.domain.repository;

import com.workshop.passenger.domain.model.aggregates.Passenger;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface PassengerCommandRepository extends ReactiveMongoRepository<Passenger, String> {
}
