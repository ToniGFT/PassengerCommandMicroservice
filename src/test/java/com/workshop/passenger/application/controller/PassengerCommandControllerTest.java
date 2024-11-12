package com.workshop.passenger.application.controller;

import com.workshop.passenger.application.dto.PassengerUpdateDTO;
import com.workshop.passenger.application.response.service.PassengerResponseService;
import com.workshop.passenger.application.services.PassengerCommandService;
import com.workshop.passenger.domain.model.aggregates.Passenger;
import com.workshop.passenger.domain.model.entities.Trip;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@DisplayName("PassengerCommandController Unit Tests")
class PassengerCommandControllerTest {

    @Mock
    private PassengerCommandService passengerCommandService;

    @Mock
    private PassengerResponseService passengerResponseService;

    @InjectMocks
    private PassengerCommandController passengerCommandController;

    private Passenger passenger;
    private PassengerUpdateDTO passengerUpdateDTO;
    private Trip trip;
    private String passengerId;
    private String tripId;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        passengerId = new ObjectId().toHexString();
        tripId = new ObjectId().toHexString();

        passenger = Passenger.builder()
                .name("John Doe")
                .email("john.doe@example.com")
                .phone("123-456-7890")
                .preferredPaymentMethod("Credit Card")
                .trips(null)
                .build();

        passengerUpdateDTO = PassengerUpdateDTO.builder()
                .name("John Doe")
                .email("john.doe@example.com")
                .phone("123-456-7890")
                .preferredPaymentMethod("Credit Card")
                .trips(null)
                .build();

        trip = Trip.builder()
                .tripId(tripId)
                .routeId("route456")
                .vehicleId("vehicle789")
                .startStop("Stop A")
                .endStop("Stop B")
                .fare(10.0)
                .build();
    }

    @Test
    @DisplayName("Create Passenger - Should Return Created Response")
    void createPassenger_shouldReturnCreatedResponse() {
        when(passengerCommandService.createPassenger(any(Passenger.class))).thenReturn(Mono.just(passenger));
        when(passengerResponseService.buildCreatedResponse(passenger))
                .thenReturn(Mono.just(ResponseEntity.status(201).body(passenger)));

        Mono<ResponseEntity<Passenger>> result = passengerCommandController.createPassenger(passenger);

        StepVerifier.create(result)
                .expectNextMatches(response -> response.getBody() != null && response.getBody().getName().equals("John Doe"))
                .verifyComplete();
    }

    @Test
    @DisplayName("Update Passenger - Should Return OK Response")
    void updatePassenger_shouldReturnOkResponse() {
        when(passengerCommandService.updatePassenger(eq(passengerId), any(PassengerUpdateDTO.class))).thenReturn(Mono.just(passenger));
        when(passengerResponseService.buildOkResponse(passenger))
                .thenReturn(Mono.just(ResponseEntity.ok(passenger)));

        Mono<ResponseEntity<Passenger>> result = passengerCommandController.updatePassenger(passengerId, passengerUpdateDTO);

        StepVerifier.create(result)
                .expectNextMatches(response -> response.getBody() != null && response.getBody().getName().equals("John Doe"))
                .verifyComplete();
    }

    @Test
    @DisplayName("Delete Passenger - Should Return No Content Response")
    void deletePassenger_shouldReturnNoContentResponse() {
        when(passengerCommandService.deletePassenger(passengerId)).thenReturn(Mono.empty());
        when(passengerResponseService.buildNoContentResponse()).thenReturn(Mono.just(ResponseEntity.noContent().build()));

        Mono<ResponseEntity<Void>> result = passengerCommandController.deletePassenger(passengerId);

        StepVerifier.create(result)
                .expectNextMatches(response -> response.getStatusCode().is2xxSuccessful())
                .verifyComplete();
    }

    @Test
    @DisplayName("Add Trip to Passenger - Should Return OK Response")
    void addTripToPassenger_shouldReturnOkResponse() {
        when(passengerCommandService.addTripToPassenger(eq(passengerId), any(Trip.class))).thenReturn(Mono.just(passenger));
        when(passengerResponseService.buildOkResponse(passenger))
                .thenReturn(Mono.just(ResponseEntity.ok(passenger)));

        Mono<ResponseEntity<Passenger>> result = passengerCommandController.addTripToPassenger(passengerId, trip);

        StepVerifier.create(result)
                .expectNextMatches(response -> response.getBody() != null && response.getBody().getName().equals("John Doe"))
                .verifyComplete();
    }

    @Test
    @DisplayName("Remove Trip from Passenger - Should Return OK Response")
    void removeTripFromPassenger_shouldReturnOkResponse() {
        when(passengerCommandService.removeTripFromPassenger(eq(passengerId), eq(tripId))).thenReturn(Mono.just(passenger));
        when(passengerResponseService.buildOkResponse(passenger))
                .thenReturn(Mono.just(ResponseEntity.ok(passenger)));

        Mono<ResponseEntity<Passenger>> result = passengerCommandController.removeTripFromPassenger(passengerId, tripId);

        StepVerifier.create(result)
                .expectNextMatches(response -> response.getBody() != null && response.getBody().getName().equals("John Doe"))
                .verifyComplete();
    }
}
