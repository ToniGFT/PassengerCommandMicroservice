package com.workshop.passenger.application.services;

import com.workshop.passenger.application.dto.PassengerUpdateDTO;
import com.workshop.passenger.domain.model.aggregates.Passenger;
import com.workshop.passenger.domain.model.entities.Trip;
import reactor.core.publisher.Mono;

public interface PassengerCommandService {

    Mono<Passenger> createPassenger(Passenger passenger);

    Mono<Passenger> updatePassenger(String passengerId, PassengerUpdateDTO updatedPassengerDto);

    Mono<Void> deletePassenger(String passengerId);

    Mono<Passenger> addTripToPassenger(String passengerId, Trip trip);

    Mono<Passenger> removeTripFromPassenger(String passengerId, String tripId);
}
