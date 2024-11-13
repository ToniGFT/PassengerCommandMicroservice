package com.workshop.passenger.domain.operations;

import com.workshop.passenger.domain.exception.PassengerNotFoundException;
import com.workshop.passenger.domain.model.aggregates.Passenger;
import com.workshop.passenger.domain.repository.PassengerCommandRepository;
import reactor.core.publisher.Mono;

public class PassengerValidator {

    private PassengerValidator() {
    }

    public static Mono<Passenger> findPassengerById(PassengerCommandRepository passengerCommandRepository, String passengerId) {
        return passengerCommandRepository.findById(passengerId)
                .switchIfEmpty(Mono.error(new PassengerNotFoundException("Passenger not found for ID: " + passengerId)));
    }
}
