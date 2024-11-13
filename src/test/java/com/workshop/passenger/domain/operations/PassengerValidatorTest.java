package com.workshop.passenger.domain.operations;

import com.workshop.passenger.domain.exception.PassengerNotFoundException;
import com.workshop.passenger.domain.model.aggregates.Passenger;
import com.workshop.passenger.domain.repository.PassengerCommandRepository;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

class PassengerValidatorTest {

    @Mock
    private PassengerCommandRepository passengerCommandRepository;

    private Passenger passenger;
    private String passengerId;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        passengerId = new ObjectId().toHexString();

        passenger = Passenger.builder()
                .id(new ObjectId(passengerId))
                .name("John Doe")
                .email("john.doe@example.com")
                .phone("123-456-7890")
                .preferredPaymentMethod("Credit Card")
                .build();
    }

    @Test
    @DisplayName("Test findPassengerById - Passenger Found Successfully")
    void testFindPassengerById_Success() {
        when(passengerCommandRepository.findById(passengerId)).thenReturn(Mono.just(passenger));

        StepVerifier.create(PassengerValidator.findPassengerById(passengerCommandRepository, passengerId))
                .expectNextMatches(foundPassenger -> foundPassenger.getId().toHexString().equals(passengerId))
                .verifyComplete();
    }

    @Test
    @DisplayName("Test findPassengerById - Passenger Not Found")
    void testFindPassengerById_NotFound() {
        when(passengerCommandRepository.findById(anyString())).thenReturn(Mono.empty());

        StepVerifier.create(PassengerValidator.findPassengerById(passengerCommandRepository, passengerId))
                .expectErrorMatches(throwable -> throwable instanceof PassengerNotFoundException)
                .verify();
    }
}
