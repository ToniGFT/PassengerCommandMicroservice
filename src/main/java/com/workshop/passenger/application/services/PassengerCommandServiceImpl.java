package com.workshop.passenger.application.services;

import com.workshop.passenger.domain.model.aggregates.Passenger;
import com.workshop.passenger.domain.model.entities.Trip;
import com.workshop.passenger.domain.model.mapper.PassengerMapper;
import com.workshop.passenger.domain.model.operations.PassengerTripOperations;
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
    public Mono<Passenger> updatePassenger(String passengerId, Passenger updatedPassenger) {
        return passengerCommandRepository.findById(passengerId)
                .flatMap(existingPassenger -> {
                    PassengerMapper.mapToExistingPassenger(updatedPassenger, existingPassenger);
                    return passengerCommandRepository.save(existingPassenger);
                });
    }

    @Override
    public Mono<Void> deletePassenger(String passengerId) {
        return passengerCommandRepository.deleteById(passengerId);
    }

    @Override
    public Mono<Passenger> addTripToPassenger(String passengerId, Trip trip) {
        return passengerCommandRepository.findById(passengerId)
                .flatMap(passenger -> {
                    PassengerTripOperations.addTrip(passenger, trip);
                    return passengerCommandRepository.save(passenger);
                });
    }

    @Override
    public Mono<Passenger> removeTripFromPassenger(String passengerId, String tripId) {
        return passengerCommandRepository.findById(passengerId)
                .flatMap(passenger -> {
                    PassengerTripOperations.removeTrip(passenger, tripId);
                    return passengerCommandRepository.save(passenger);
                });
    }

}
