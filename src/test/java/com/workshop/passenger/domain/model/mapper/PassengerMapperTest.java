package com.workshop.passenger.domain.model.mapper;

import com.workshop.passenger.domain.model.aggregates.Passenger;
import com.workshop.passenger.domain.model.entities.Trip;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("PassengerMapper Unit Tests")
class PassengerMapperTest {

    private Passenger sourcePassenger;
    private Passenger targetPassenger;

    @BeforeEach
    void setUp() {
        sourcePassenger = Passenger.builder()
                .name("John Doe")
                .email("john.doe@example.com")
                .phone("123-456-7890")
                .preferredPaymentMethod("Credit Card")
                .registeredAt(LocalDateTime.now())
                .trips(List.of(
                        Trip.builder()
                                .tripId("trip123")
                                .routeId("route456")
                                .vehicleId("vehicle789")
                                .startTime(LocalDateTime.now().minusHours(1))
                                .endTime(LocalDateTime.now())
                                .startStop("Stop A")
                                .endStop("Stop B")
                                .fare(15.0)
                                .build()
                ))
                .build();

        targetPassenger = Passenger.builder().build();
    }

    @Test
    @DisplayName("Map Passenger - Should Map All Fields Correctly")
    void mapToExistingPassenger_shouldMapAllFieldsCorrectly() {
        // Act
        PassengerMapper.mapToExistingPassenger(sourcePassenger, targetPassenger);

        // Assert
        assertThat(targetPassenger.getName()).isEqualTo(sourcePassenger.getName());
        assertThat(targetPassenger.getEmail()).isEqualTo(sourcePassenger.getEmail());
        assertThat(targetPassenger.getPhone()).isEqualTo(sourcePassenger.getPhone());
        assertThat(targetPassenger.getPreferredPaymentMethod()).isEqualTo(sourcePassenger.getPreferredPaymentMethod());
        assertThat(targetPassenger.getRegisteredAt()).isEqualTo(sourcePassenger.getRegisteredAt());
        assertThat(targetPassenger.getTrips().size()).isEqualTo(sourcePassenger.getTrips().size());

        // Verify trip mapping
        assertThat(targetPassenger.getTrips().get(0).getTripId()).isEqualTo(sourcePassenger.getTrips().get(0).getTripId());
        assertThat(targetPassenger.getTrips().get(0).getRouteId()).isEqualTo(sourcePassenger.getTrips().get(0).getRouteId());
        assertThat(targetPassenger.getTrips().get(0).getVehicleId()).isEqualTo(sourcePassenger.getTrips().get(0).getVehicleId());
        assertThat(targetPassenger.getTrips().get(0).getStartTime()).isEqualTo(sourcePassenger.getTrips().get(0).getStartTime());
        assertThat(targetPassenger.getTrips().get(0).getEndTime()).isEqualTo(sourcePassenger.getTrips().get(0).getEndTime());
        assertThat(targetPassenger.getTrips().get(0).getStartStop()).isEqualTo(sourcePassenger.getTrips().get(0).getStartStop());
        assertThat(targetPassenger.getTrips().get(0).getEndStop()).isEqualTo(sourcePassenger.getTrips().get(0).getEndStop());
        assertThat(targetPassenger.getTrips().get(0).getFare()).isEqualTo(sourcePassenger.getTrips().get(0).getFare());
    }
}
