package com.workshop.passenger.domain.operations;

import com.workshop.passenger.domain.exception.PassengerNotFoundException;
import com.workshop.passenger.domain.exception.TripNotFoundException;
import com.workshop.passenger.domain.model.entities.Trip;
import com.workshop.passenger.infraestructure.Route.service.RouteService;
import com.workshop.passenger.infraestructure.Vehicle.service.VehicleService;
import reactor.core.publisher.Mono;

public class PassengerValidationOperations {

    private PassengerValidationOperations() {
    }

    public static Mono<Void> validateTripDependencies(Trip trip, RouteService routeService, VehicleService vehicleService) {
        return Mono.zip(
                routeService.getRouteById(trip.getRouteId())
                        .switchIfEmpty(passengerNotFound("Route not found for ID: " + trip.getRouteId())),
                vehicleService.getVehicleById(trip.getVehicleId())
                        .switchIfEmpty(passengerNotFound("Vehicle not found for ID: " + trip.getVehicleId()))
        ).then();
    }

    public static <T> Mono<T> passengerNotFound(String passengerId) {
        return Mono.error(new PassengerNotFoundException(passengerId));
    }

    public static <T> Mono<T> tripNotFound(String tripId) {
        return Mono.error(new TripNotFoundException(tripId));
    }
}
