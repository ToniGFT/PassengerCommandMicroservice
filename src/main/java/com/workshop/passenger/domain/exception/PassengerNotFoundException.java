package com.workshop.passenger.domain.exception;

public class PassengerNotFoundException extends RuntimeException {
    public PassengerNotFoundException(String passengerId) {
        super("Passenger not found with ID: " + passengerId);
    }
}
