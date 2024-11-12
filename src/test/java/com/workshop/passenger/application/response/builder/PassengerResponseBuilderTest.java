package com.workshop.passenger.application.response.builder;

import com.workshop.passenger.domain.model.aggregates.Passenger;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("PassengerResponseBuilder Unit Tests")
class PassengerResponseBuilderTest {

    @Test
    @DisplayName("Generate Created Response - Should Return 201 Created with Passenger")
    void generateCreatedResponse_shouldReturnCreatedResponse() {
        // given
        Passenger passenger = Passenger.builder()
                .name("John Doe")
                .email("john.doe@example.com")
                .phone("123-456-7890")
                .preferredPaymentMethod("Credit Card")
                .trips(null)
                .build();

        // when
        ResponseEntity<Passenger> response = PassengerResponseBuilder.generateCreatedResponse(passenger);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getName()).isEqualTo("John Doe");
        assertThat(response.getBody().getEmail()).isEqualTo("john.doe@example.com");
    }

    @Test
    @DisplayName("Generate OK Response - Should Return 200 OK with Passenger")
    void generateOkResponse_shouldReturnOkResponse() {
        // given
        Passenger passenger = Passenger.builder()
                .name("Jane Doe")
                .email("jane.doe@example.com")
                .phone("987-654-3210")
                .preferredPaymentMethod("Debit Card")
                .trips(null)
                .build();

        // when
        ResponseEntity<Passenger> response = PassengerResponseBuilder.generateOkResponse(passenger);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getName()).isEqualTo("Jane Doe");
        assertThat(response.getBody().getEmail()).isEqualTo("jane.doe@example.com");
    }

    @Test
    @DisplayName("Generate No Content Response - Should Return 204 No Content")
    void generateNoContentResponse_shouldReturnNoContentResponse() {
        // when
        ResponseEntity<Void> response = PassengerResponseBuilder.generateNoContentResponse();

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        assertThat(response.getBody()).isNull();
    }
}
