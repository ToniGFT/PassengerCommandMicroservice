package com.workshop.passenger.application.services;

import com.workshop.passenger.domain.model.aggregates.Passenger;
import com.workshop.passenger.domain.model.entities.Trip;
import com.workshop.passenger.domain.repository.PassengerCommandRepository;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.ArrayList;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DisplayName("PassengerCommandServiceImpl Unit Tests")
class PassengerCommandServiceImplTests {

    @InjectMocks
    private PassengerCommandServiceImpl passengerService;

    @Mock
    private PassengerCommandRepository passengerCommandRepository;

    private Passenger passenger;
    private Trip trip;
    private String passengerId;
    private String tripId;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        passengerId = ObjectId.get().toHexString();
        tripId = ObjectId.get().toHexString();

        passenger = Passenger.builder()
                .name("John Doe")
                .email("john.doe@example.com")
                .phone("123-456-7890")
                .preferredPaymentMethod("Credit Card")
                .trips(new ArrayList<>())
                .build();

        trip = Trip.builder()
                .tripId(tripId)
                .routeId("route123")
                .vehicleId("vehicle123")
                .startStop("A")
                .endStop("B")
                .fare(10.0)
                .build();
    }

    @Test
    @DisplayName("Test createPassenger - Successful Save")
    void testCreatePassenger_Success() {
        when(passengerCommandRepository.save(any(Passenger.class))).thenReturn(Mono.just(passenger));

        Mono<Passenger> result = passengerService.createPassenger(passenger);

        StepVerifier.create(result)
                .expectNextMatches(savedPassenger -> savedPassenger.getName().equals("John Doe"))
                .verifyComplete();

        verify(passengerCommandRepository, times(1)).save(any(Passenger.class));
    }

    @Test
    @DisplayName("Test updatePassenger - Successful Update")
    void testUpdatePassenger_Success() {
        Passenger updatedPassenger = Passenger.builder()
                .name("Jane Doe")
                .email("jane.doe@example.com")
                .phone("987-654-3210")
                .build();

        when(passengerCommandRepository.findById(passengerId)).thenReturn(Mono.just(passenger));
        when(passengerCommandRepository.save(any(Passenger.class))).thenReturn(Mono.just(passenger));

        Mono<Passenger> result = passengerService.updatePassenger(passengerId, updatedPassenger);

        StepVerifier.create(result)
                .expectNextMatches(updated -> updated.getName().equals("Jane Doe"))
                .verifyComplete();

        verify(passengerCommandRepository, times(1)).findById(passengerId);
        verify(passengerCommandRepository, times(1)).save(any(Passenger.class));
    }

    @Test
    @DisplayName("Test deletePassenger - Successful Deletion")
    void testDeletePassenger_Success() {
        when(passengerCommandRepository.deleteById(passengerId)).thenReturn(Mono.empty());

        Mono<Void> result = passengerService.deletePassenger(passengerId);

        StepVerifier.create(result)
                .verifyComplete();

        verify(passengerCommandRepository, times(1)).deleteById(passengerId);
    }

    @Test
    @DisplayName("Test addTripToPassenger - Trip Added Successfully")
    void testAddTripToPassenger_Success() {
        when(passengerCommandRepository.findById(passengerId)).thenReturn(Mono.just(passenger));
        when(passengerCommandRepository.save(any(Passenger.class))).thenReturn(Mono.just(passenger));

        Mono<Passenger> result = passengerService.addTripToPassenger(passengerId, trip);

        StepVerifier.create(result)
                .expectNextMatches(passengerWithTrip -> passengerWithTrip.getTrips().contains(trip))
                .verifyComplete();

        verify(passengerCommandRepository, times(1)).findById(passengerId);
        verify(passengerCommandRepository, times(1)).save(any(Passenger.class));
    }

    @Test
    @DisplayName("Test removeTripFromPassenger - Trip Removed Successfully")
    void testRemoveTripFromPassenger_Success() {
        passenger.getTrips().add(trip);

        when(passengerCommandRepository.findById(passengerId)).thenReturn(Mono.just(passenger));
        when(passengerCommandRepository.save(any(Passenger.class))).thenReturn(Mono.just(passenger));

        Mono<Passenger> result = passengerService.removeTripFromPassenger(passengerId, tripId);

        StepVerifier.create(result)
                .expectNextMatches(passengerWithoutTrip -> passengerWithoutTrip.getTrips().isEmpty())
                .verifyComplete();

        verify(passengerCommandRepository, times(1)).findById(passengerId);
        verify(passengerCommandRepository, times(1)).save(any(Passenger.class));
    }
}
