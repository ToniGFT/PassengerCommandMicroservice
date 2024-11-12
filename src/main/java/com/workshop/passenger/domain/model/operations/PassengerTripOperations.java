package com.workshop.passenger.domain.model.operations;

import com.workshop.passenger.domain.model.aggregates.Passenger;
import com.workshop.passenger.domain.model.entities.Trip;

public class PassengerTripOperations {

    private PassengerTripOperations() {
    }

    public static void addTrip(Passenger passenger, Trip trip) {
        passenger.getTrips().add(trip);
    }

    public static void removeTrip(Passenger passenger, String tripId) {
        passenger.getTrips().removeIf(trip -> trip.getTripId().equals(tripId));
    }
}
