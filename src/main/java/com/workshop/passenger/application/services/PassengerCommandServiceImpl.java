package com.workshop.passenger.application.services;

import com.workshop.passenger.application.dto.PassengerUpdateDTO;
import com.workshop.passenger.domain.model.aggregates.Passenger;
import com.workshop.passenger.domain.model.entities.Trip;
import com.workshop.passenger.domain.model.mapper.PassengerMapper;
import com.workshop.passenger.domain.operations.PassengerTripOperations;
import com.workshop.passenger.domain.operations.PassengerTripValidator;
import com.workshop.passenger.domain.operations.PassengerValidator;
import com.workshop.passenger.domain.repository.PassengerCommandRepository;
import com.workshop.passenger.infraestructure.Route.service.RouteService;
import com.workshop.passenger.infraestructure.Vehicle.service.VehicleService;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class PassengerCommandServiceImpl implements PassengerCommandService {

    private final PassengerCommandRepository passengerCommandRepository;
    private final RouteService routeService;
    private final VehicleService vehicleService;

    public PassengerCommandServiceImpl(PassengerCommandRepository passengerCommandRepository,
                                       RouteService routeService,
                                       VehicleService vehicleService) {
        this.passengerCommandRepository = passengerCommandRepository;
        this.routeService = routeService;
        this.vehicleService = vehicleService;
    }

    @Override
    public Mono<Passenger> createPassenger(Passenger passenger) {
        return PassengerTripValidator.validateTripsInPassenger(passenger, routeService, vehicleService)
                .then(passengerCommandRepository.save(passenger));
    }

    @Override
    public Mono<Passenger> updatePassenger(String passengerId, PassengerUpdateDTO updatedPassengerDto) {
        return PassengerValidator.findPassengerById(passengerCommandRepository, passengerId)
                .flatMap(existingPassenger -> {
                    PassengerMapper.mapToExistingPassenger(updatedPassengerDto, existingPassenger);
                    PassengerTripValidator.initializeTripsIfNull(existingPassenger);
                    return PassengerTripValidator.validateTripsInPassenger(existingPassenger, routeService, vehicleService)
                            .then(passengerCommandRepository.save(existingPassenger));
                });
    }


    @Override
    public Mono<Void> deletePassenger(String passengerId) {
        return PassengerValidator.findPassengerById(passengerCommandRepository, passengerId)
                .flatMap(passenger -> passengerCommandRepository.deleteById(passengerId).then());
    }


    @Override
    public Mono<Passenger> addTripToPassenger(String passengerId, Trip trip) {
        return PassengerValidator.findPassengerById(passengerCommandRepository, passengerId)
                .flatMap(passenger ->
                        PassengerTripValidator.validateTripDependencies(trip, routeService, vehicleService)
                                .then(Mono.defer(() -> {
                                    PassengerTripOperations.addTrip(passenger, trip);
                                    return passengerCommandRepository.save(passenger);
                                }))
                );
    }

    @Override
    public Mono<Passenger> removeTripFromPassenger(String passengerId, String tripId) {
        return PassengerValidator.findPassengerById(passengerCommandRepository, passengerId)
                .flatMap(passenger -> PassengerTripValidator.validateAndRemoveTrip(passenger, tripId))
                .flatMap(passengerCommandRepository::save);
    }
}
