package com.workshop.passenger.application.response.builder;

import com.workshop.passenger.domain.model.aggregates.Passenger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class PassengerResponseBuilder {

    public static ResponseEntity<Passenger> generateCreatedResponse(Passenger passenger) {
        return ResponseEntity.status(HttpStatus.CREATED).body(passenger);
    }

    public static ResponseEntity<Passenger> generateOkResponse(Passenger passenger) {
        return ResponseEntity.ok(passenger);
    }

    public static ResponseEntity<Void> generateNoContentResponse() {
        return ResponseEntity.noContent().build();
    }
}
