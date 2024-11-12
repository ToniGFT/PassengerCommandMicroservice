package com.workshop.passenger.domain.exception;

public class TripNotFoundException extends RuntimeException {
    public TripNotFoundException(String tripId) {
        super("Trip not found with ID: " + tripId);
    }
}
