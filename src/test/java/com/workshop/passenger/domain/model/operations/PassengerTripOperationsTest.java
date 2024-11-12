package com.workshop.passenger.domain.model.operations;

import com.workshop.passenger.domain.model.aggregates.Passenger;
import com.workshop.passenger.domain.model.entities.Trip;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("PassengerTripOperations Unit Tests")
class PassengerTripOperationsTest {

    private Passenger passenger;
    private Trip trip1;
    private Trip trip2;

    @BeforeEach
    void setUp() {
        passenger = Passenger.builder()
                .name("John Doe")
                .trips(new ArrayList<>())
                .build();

        trip1 = Trip.builder()
                .tripId("trip123")
                .routeId("route456")
                .vehicleId("vehicle789")
                .startTime(LocalDateTime.now().minusHours(1))
                .endTime(LocalDateTime.now())
                .startStop("Stop A")
                .endStop("Stop B")
                .fare(10.0)
                .build();

        trip2 = Trip.builder()
                .tripId("trip456")
                .routeId("route789")
                .vehicleId("vehicle012")
                .startTime(LocalDateTime.now().minusHours(2))
                .endTime(LocalDateTime.now().minusHours(1))
                .startStop("Stop C")
                .endStop("Stop D")
                .fare(15.0)
                .build();
    }

    @Test
    @DisplayName("Add Trip - Should Add Trip to Passenger's Trip List")
    void addTrip_shouldAddTripToPassenger() {
        // Act
        PassengerTripOperations.addTrip(passenger, trip1);

        // Assert
        assertThat(passenger.getTrips()).contains(trip1);
        assertThat(passenger.getTrips().size()).isEqualTo(1);
    }

    @Test
    @DisplayName("Remove Trip - Should Remove Trip from Passenger's Trip List")
    void removeTrip_shouldRemoveTripFromPassenger() {
        // Arrange
        passenger.getTrips().add(trip1);
        passenger.getTrips().add(trip2);

        // Act
        PassengerTripOperations.removeTrip(passenger, trip1.getTripId());

        // Assert
        assertThat(passenger.getTrips()).doesNotContain(trip1);
        assertThat(passenger.getTrips()).contains(trip2);
        assertThat(passenger.getTrips().size()).isEqualTo(1);
    }

    @Test
    @DisplayName("Remove Trip - Should Not Remove Any Trip if ID Not Found")
    void removeTrip_shouldNotRemoveAnyTripIfIdNotFound() {
        // Arrange
        passenger.getTrips().add(trip1);

        // Act
        PassengerTripOperations.removeTrip(passenger, "nonexistentTripId");

        // Assert
        assertThat(passenger.getTrips()).contains(trip1);
        assertThat(passenger.getTrips().size()).isEqualTo(1);
    }
}
