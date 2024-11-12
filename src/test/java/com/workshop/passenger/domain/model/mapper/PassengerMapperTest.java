package com.workshop.passenger.domain.model.mapper;

import com.workshop.passenger.application.dto.PassengerUpdateDTO;
import com.workshop.passenger.domain.model.aggregates.Passenger;
import com.workshop.passenger.domain.model.entities.Trip;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("PassengerMapper Unit Tests")
class PassengerMapperTest {

    private PassengerUpdateDTO sourcePassengerDTO;
    private Passenger targetPassenger;

    @BeforeEach
    void setUp() {
        sourcePassengerDTO = PassengerUpdateDTO.builder()
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

        targetPassenger = Passenger.builder()
                .id(new ObjectId("507f1f77bcf86cd799439011")) // Set an existing ObjectId for targetPassenger
                .build();
    }

    @Test
    @DisplayName("Map PassengerUpdateDTO to Existing Passenger - Should Map All Fields Correctly Except ID")
    void mapToExistingPassenger_shouldMapAllFieldsCorrectlyExceptId() {
        // Act
        PassengerMapper.mapToExistingPassenger(sourcePassengerDTO, targetPassenger);

        // Assert
        assertThat(targetPassenger.getName()).isEqualTo(sourcePassengerDTO.getName());
        assertThat(targetPassenger.getEmail()).isEqualTo(sourcePassengerDTO.getEmail());
        assertThat(targetPassenger.getPhone()).isEqualTo(sourcePassengerDTO.getPhone());
        assertThat(targetPassenger.getPreferredPaymentMethod()).isEqualTo(sourcePassengerDTO.getPreferredPaymentMethod());
        assertThat(targetPassenger.getRegisteredAt()).isEqualTo(sourcePassengerDTO.getRegisteredAt());
        assertThat(targetPassenger.getTrips().size()).isEqualTo(sourcePassengerDTO.getTrips().size());

        // Verify trip mapping
        assertThat(targetPassenger.getTrips().get(0).getTripId()).isEqualTo(sourcePassengerDTO.getTrips().get(0).getTripId());
        assertThat(targetPassenger.getTrips().get(0).getRouteId()).isEqualTo(sourcePassengerDTO.getTrips().get(0).getRouteId());
        assertThat(targetPassenger.getTrips().get(0).getVehicleId()).isEqualTo(sourcePassengerDTO.getTrips().get(0).getVehicleId());
        assertThat(targetPassenger.getTrips().get(0).getStartTime()).isEqualTo(sourcePassengerDTO.getTrips().get(0).getStartTime());
        assertThat(targetPassenger.getTrips().get(0).getEndTime()).isEqualTo(sourcePassengerDTO.getTrips().get(0).getEndTime());
        assertThat(targetPassenger.getTrips().get(0).getStartStop()).isEqualTo(sourcePassengerDTO.getTrips().get(0).getStartStop());
        assertThat(targetPassenger.getTrips().get(0).getEndStop()).isEqualTo(sourcePassengerDTO.getTrips().get(0).getEndStop());
        assertThat(targetPassenger.getTrips().get(0).getFare()).isEqualTo(sourcePassengerDTO.getTrips().get(0).getFare());

        // Verify that ObjectId is not modified
        assertThat(targetPassenger.getId()).isEqualTo(new ObjectId("507f1f77bcf86cd799439011"));
    }
}
