package com.workshop.passenger.application.controller;

import com.workshop.passenger.application.dto.PassengerUpdateDTO;
import com.workshop.passenger.domain.model.aggregates.Passenger;
import com.workshop.passenger.domain.model.entities.Trip;
import com.workshop.passenger.domain.repository.PassengerCommandRepository;
import com.workshop.passenger.infraestructure.Route.service.RouteService;
import com.workshop.passenger.infraestructure.Vehicle.service.VehicleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureWebTestClient
@DisplayName("PassengerCommandController Integration Tests")
class PassengerCommandControllerIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private PassengerCommandRepository passengerCommandRepository;

    @MockBean
    private RouteService routeService;

    @MockBean
    private VehicleService vehicleService;


    private Passenger passenger;
    private Trip trip;

    @BeforeEach
    void setUp() {
        passengerCommandRepository.deleteAll().block();

        trip = Trip.builder()
                .tripId("trip123")
                .routeId("route456")
                .vehicleId("vehicle789")
                .startStop("Stop A")
                .endStop("Stop B")
                .fare(10.0)
                .build();

        passenger = Passenger.builder()
                .name("John Doe")
                .email("john.doe@example.com")
                .phone("123-456-7890")
                .preferredPaymentMethod("Credit Card")
                .trips(new ArrayList<>(List.of(trip)))
                .build();
    }


    @Test
    @DisplayName("Create Passenger - Should Return 201 Created")
    void createPassenger_shouldReturnCreated() {
        webTestClient.post()
                .uri("/passengers")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(passenger), Passenger.class)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(Passenger.class)
                .value(createdPassenger -> {
                    assertThat(createdPassenger.getName()).isEqualTo("John Doe");
                    assertThat(createdPassenger.getEmail()).isEqualTo("john.doe@example.com");
                });
    }

    @Test
    @DisplayName("Update Passenger - Should Return 200 OK")
    void updatePassenger_shouldReturnOk() {
        Passenger savedPassenger = passengerCommandRepository.save(passenger).block();
        PassengerUpdateDTO passengerUpdateDTO = PassengerUpdateDTO.builder()
                .name("Jane Doe")
                .email("jane.doe@example.com")
                .phone("098-765-4321")
                .preferredPaymentMethod("Debit Card")
                .build();

        webTestClient.put()
                .uri("/passengers/{id}", savedPassenger.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(passengerUpdateDTO), PassengerUpdateDTO.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Passenger.class)
                .value(updatedPassenger -> assertThat(updatedPassenger.getName()).isEqualTo("Jane Doe"));
    }

    @Test
    @DisplayName("Delete Passenger - Should Return 204 No Content")
    void deletePassenger_shouldReturnNoContent() {
        Passenger savedPassenger = passengerCommandRepository.save(passenger).block();

        webTestClient.delete()
                .uri("/passengers/{id}", savedPassenger.getId())
                .exchange()
                .expectStatus().isNoContent();

        assertThat(passengerCommandRepository.findById(String.valueOf(savedPassenger.getId())).block()).isNull();
    }

    @Test
    @DisplayName("Add Trip to Passenger - Should Return 200 OK")
    void addTripToPassenger_shouldReturnOk() {
        Passenger savedPassenger = passengerCommandRepository.save(passenger).block();

        webTestClient.post()
                .uri("/passengers/{id}/trips", savedPassenger.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(trip), Trip.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Passenger.class)
                .value(updatedPassenger -> assertThat(updatedPassenger.getTrips()).contains(trip));
    }

    @Test
    @DisplayName("Remove Trip from Passenger - Should Return 200 OK")
    void removeTripFromPassenger_shouldReturnOk() {
        passenger.getTrips().add(trip);
        Passenger savedPassenger = passengerCommandRepository.save(passenger).block();

        assert savedPassenger != null;
        webTestClient.delete()
                .uri("/passengers/{passengerId}/trips/{tripId}", savedPassenger.getId(), trip.getTripId())
                .exchange()
                .expectStatus().isOk()
                .expectBody(Passenger.class)
                .value(updatedPassenger -> assertThat(updatedPassenger.getTrips()).doesNotContain(trip));
    }
}
