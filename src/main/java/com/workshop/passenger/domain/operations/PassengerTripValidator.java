package com.workshop.passenger.domain.operations;

import com.workshop.passenger.domain.exception.PassengerNotFoundException;
import com.workshop.passenger.domain.exception.TripNotFoundException;
import com.workshop.passenger.domain.model.aggregates.Passenger;
import com.workshop.passenger.domain.model.entities.Trip;
import com.workshop.passenger.infraestructure.Route.service.RouteService;
import com.workshop.passenger.infraestructure.Vehicle.service.VehicleService;
import reactor.core.publisher.Mono;

import java.util.ArrayList;

public class PassengerTripValidator {

    private PassengerTripValidator() {
    }

    public static Mono<Passenger> validateAndRemoveTrip(Passenger passenger, String tripId) {
        if (!PassengerTripOperations.hasTrip(passenger, tripId)) {
            return Mono.error(new TripNotFoundException(tripId));
        }
        PassengerTripOperations.removeTrip(passenger, tripId);
        return Mono.just(passenger);
    }

    public static Mono<Void> validateTripsInPassenger(Passenger passenger, RouteService routeService, VehicleService vehicleService) {
        return Mono.when(passenger.getTrips().stream()
                .map(trip -> validateTripDependencies(trip, routeService, vehicleService))
                .toArray(Mono[]::new));
    }

    public static Mono<Void> validateTripDependencies(Trip trip, RouteService routeService, VehicleService vehicleService) {
        return Mono.zip(
                routeService.getRouteById(trip.getRouteId())
                        .switchIfEmpty(Mono.error(new PassengerNotFoundException("Route not found for ID: " + trip.getRouteId()))),
                vehicleService.getVehicleById(trip.getVehicleId())
                        .switchIfEmpty(Mono.error(new PassengerNotFoundException("Vehicle not found for ID: " + trip.getVehicleId())))
        ).then();
    }

    public static void initializeTripsIfNull(Passenger passenger) {
        if (passenger.getTrips() == null) {
            passenger.setTrips(new ArrayList<>());
        }
    }
}
