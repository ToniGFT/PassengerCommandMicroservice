package com.workshop.passenger.application.response.service;

import com.workshop.passenger.domain.model.aggregates.Passenger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@DisplayName("PassengerResponseService Unit Tests")
class PassengerResponseServiceTest {

    @InjectMocks
    private PassengerResponseService passengerResponseService;

    private Passenger passenger;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        passenger = Passenger.builder()
                .name("John Doe")
                .email("john.doe@example.com")
                .phone("123-456-7890")
                .preferredPaymentMethod("Credit Card")
                .trips(null)
                .build();
    }

    @Test
    @DisplayName("Build Created Response - Should Return Mono with 201 Created")
    void buildCreatedResponse_shouldReturnCreatedResponse() {
        // when
        Mono<ResponseEntity<Passenger>> result = passengerResponseService.buildCreatedResponse(passenger);

        // then
        StepVerifier.create(result)
                .expectNextMatches(response -> response.getStatusCode() == HttpStatus.CREATED &&
                        response.getBody() != null &&
                        response.getBody().getName().equals("John Doe"))
                .verifyComplete();
    }

    @Test
    @DisplayName("Build OK Response - Should Return Mono with 200 OK")
    void buildOkResponse_shouldReturnOkResponse() {
        // when
        Mono<ResponseEntity<Passenger>> result = passengerResponseService.buildOkResponse(passenger);

        // then
        StepVerifier.create(result)
                .expectNextMatches(response -> response.getStatusCode() == HttpStatus.OK &&
                        response.getBody() != null &&
                        response.getBody().getName().equals("John Doe"))
                .verifyComplete();
    }

    @Test
    @DisplayName("Build No Content Response - Should Return Mono with 204 No Content")
    void buildNoContentResponse_shouldReturnNoContentResponse() {
        // when
        Mono<ResponseEntity<Void>> result = passengerResponseService.buildNoContentResponse();

        // then
        StepVerifier.create(result)
                .expectNextMatches(response -> response.getStatusCode() == HttpStatus.NO_CONTENT &&
                        response.getBody() == null)
                .verifyComplete();
    }
}
