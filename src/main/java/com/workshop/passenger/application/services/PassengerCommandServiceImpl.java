package com.workshop.passenger.application.services;

import com.workshop.passenger.application.dto.PassengerUpdateDTO;
import com.workshop.passenger.domain.exception.PassengerNotFoundException;
import com.workshop.passenger.domain.exception.TripNotFoundException;
import com.workshop.passenger.domain.model.aggregates.Passenger;
import com.workshop.passenger.domain.model.entities.Trip;
import com.workshop.passenger.domain.model.mapper.PassengerMapper;
import com.workshop.passenger.domain.operations.PassengerTripOperations;
import com.workshop.passenger.domain.repository.PassengerCommandRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class PassengerCommandServiceImpl implements PassengerCommandService {

    private final PassengerCommandRepository passengerCommandRepository;

    public PassengerCommandServiceImpl(PassengerCommandRepository passengerCommandRepository) {
        this.passengerCommandRepository = passengerCommandRepository;
    }

    @Override
    public Mono<Passenger> createPassenger(Passenger passenger) {
        return passengerCommandRepository.save(passenger);
    }

    @Override
    public Mono<Passenger> updatePassenger(String passengerId, PassengerUpdateDTO updatedPassengerDto) {
        return passengerCommandRepository.findById(passengerId)
                .switchIfEmpty(Mono.error(new PassengerNotFoundException(passengerId)))
                .flatMap(existingPassenger -> {
                    PassengerMapper.mapToExistingPassenger(updatedPassengerDto, existingPassenger);
                    return passengerCommandRepository.save(existingPassenger);
                });
    }

    @Override
    public Mono<Void> deletePassenger(String passengerId) {
        return passengerCommandRepository.findById(passengerId)
                .switchIfEmpty(Mono.error(new PassengerNotFoundException(passengerId)))
                .flatMap(existingPassenger -> passengerCommandRepository.deleteById(passengerId));
    }

    @Override
    public Mono<Passenger> addTripToPassenger(String passengerId, Trip trip) {
        return passengerCommandRepository.findById(passengerId)
                .switchIfEmpty(Mono.error(new PassengerNotFoundException(passengerId)))
                .flatMap(passenger -> {
                    PassengerTripOperations.addTrip(passenger, trip);
                    return passengerCommandRepository.save(passenger);
                });
    }

    @Override
    public Mono<Passenger> removeTripFromPassenger(String passengerId, String tripId) {
        return passengerCommandRepository.findById(passengerId)
                .switchIfEmpty(Mono.error(new PassengerNotFoundException(passengerId)))
                .flatMap(passenger -> {
                    if (!PassengerTripOperations.hasTrip(passenger, tripId)) {
                        return Mono.error(new TripNotFoundException(tripId));
                    }
                    PassengerTripOperations.removeTrip(passenger, tripId);
                    return passengerCommandRepository.save(passenger);
                });
    }

}
