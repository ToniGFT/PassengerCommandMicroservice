package com.workshop.passenger.domain.operations;

import com.workshop.passenger.domain.exception.PassengerNotFoundException;
import com.workshop.passenger.domain.exception.TripNotFoundException;
import com.workshop.passenger.domain.model.aggregates.Passenger;
import com.workshop.passenger.domain.model.entities.Trip;
import com.workshop.passenger.infraestructure.Route.model.aggregates.Route;
import com.workshop.passenger.infraestructure.Route.model.entities.Schedule;
import com.workshop.passenger.infraestructure.Route.model.entities.Stop;
import com.workshop.passenger.infraestructure.Route.model.valueobjects.Coordinates;
import com.workshop.passenger.infraestructure.Route.model.valueobjects.WeekSchedule;
import com.workshop.passenger.infraestructure.Route.service.RouteService;
import com.workshop.passenger.infraestructure.Vehicle.model.aggregates.Vehicle;
import com.workshop.passenger.infraestructure.Vehicle.model.entities.Driver;
import com.workshop.passenger.infraestructure.Vehicle.model.valueobjects.Contact;
import com.workshop.passenger.infraestructure.Vehicle.model.valueobjects.MaintenanceDetails;
import com.workshop.passenger.infraestructure.Vehicle.model.valueobjects.enums.VehicleStatus;
import com.workshop.passenger.infraestructure.Vehicle.model.valueobjects.enums.VehicleType;
import com.workshop.passenger.infraestructure.Vehicle.service.VehicleService;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

class PassengerValidationOperationsTest {

    @Mock
    private RouteService routeService;

    @Mock
    private VehicleService vehicleService;

    private Passenger passenger;
    private Trip trip;
    private Route route;
    private Vehicle vehicle;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        String tripId = ObjectId.get().toHexString();
        ObjectId passengerId = new ObjectId();

        trip = Trip.builder()
                .tripId(tripId)
                .routeId("route123")
                .vehicleId("vehicle123")
                .startStop("A")
                .endStop("B")
                .fare(10.0)
                .build();

        passenger = Passenger.builder()
                .id(passengerId)
                .name("John Doe")
                .email("john.doe@example.com")
                .phone("123-456-7890")
                .preferredPaymentMethod("Credit Card")
                .trips(new ArrayList<>(List.of(trip)))  // Aseguramos que trips contiene el trip configurado
                .build();

        route = Route.builder()
                .routeId(new ObjectId("64f10c9e89d45e1a2b63e2bd"))
                .routeName("Route 1")
                .stops(List.of(
                        Stop.builder()
                                .stopId("stop123")
                                .stopName("Stop A")
                                .coordinates(new Coordinates(37.7749, -122.4194))
                                .arrivalTimes(List.of("08:00", "09:00"))
                                .build()
                ))
                .schedule(new Schedule(
                        new WeekSchedule(LocalTime.of(8, 0), LocalTime.of(22, 0), 15),
                        new WeekSchedule(LocalTime.of(9, 0), LocalTime.of(20, 0), 20)
                ))
                .build();

        vehicle = Vehicle.builder()
                .vehicleId(new ObjectId("64f10c5a89d45e1a2b63e2ac"))
                .licensePlate("AAA-123")
                .capacity(40)
                .currentStatus(VehicleStatus.IN_SERVICE)
                .type(VehicleType.BUS)
                .driver(Driver.builder()
                        .driverId(new ObjectId("64f10c5a89d45e1a2b63e2ac"))
                        .name("John Doe")
                        .contact(new Contact("johndoe@example.com", "+1234567890"))
                        .build())
                .maintenanceDetails(new MaintenanceDetails("Jane Smith", LocalDate.now(), "Routine checkup"))
                .currentLocation(new com.workshop.passenger.infraestructure.Vehicle.model.valueobjects.Coordinates(37.7749, -122.4194))
                .routeId(new ObjectId("64f10c9e89d45e1a2b63e2bd"))
                .build();
    }

    @Test
    @DisplayName("Test validateTripDependencies - Valid Route and Vehicle")
    void testValidateTripDependencies_Success() {
        when(routeService.getRouteById(anyString())).thenReturn(Mono.just(route)); // Simula que existe la ruta
        when(vehicleService.getVehicleById(anyString())).thenReturn(Mono.just(vehicle)); // Simula que existe el vehículo

        StepVerifier.create(PassengerValidationOperations.validateTripDependencies(trip, routeService, vehicleService))
                .verifyComplete();
    }

    @Test
    @DisplayName("Test validateTripDependencies - Route Not Found")
    void testValidateTripDependencies_RouteNotFound() {
        when(routeService.getRouteById(anyString())).thenReturn(Mono.empty());
        when(vehicleService.getVehicleById(anyString())).thenReturn(Mono.just(vehicle));

        StepVerifier.create(PassengerValidationOperations.validateTripDependencies(trip, routeService, vehicleService))
                .expectErrorMatches(throwable -> throwable instanceof PassengerNotFoundException &&
                        throwable.getMessage().contains("Route not found for ID: " + trip.getRouteId()))
                .verify();
    }

    @Test
    @DisplayName("Test validateTripDependencies - Vehicle Not Found")
    void testValidateTripDependencies_VehicleNotFound() {
        when(routeService.getRouteById(anyString())).thenReturn(Mono.just(route));
        when(vehicleService.getVehicleById(anyString())).thenReturn(Mono.empty());

        StepVerifier.create(PassengerValidationOperations.validateTripDependencies(trip, routeService, vehicleService))
                .expectErrorMatches(throwable -> throwable instanceof PassengerNotFoundException &&
                        throwable.getMessage().contains("Vehicle not found for ID: " + trip.getVehicleId()))
                .verify();
    }

    @Test
    @DisplayName("Test validateTripDependencies - Route and Vehicle Not Found")
    void testValidateTripDependencies_RouteAndVehicleNotFound() {
        when(routeService.getRouteById(anyString())).thenReturn(Mono.empty());
        when(vehicleService.getVehicleById(anyString())).thenReturn(Mono.empty());

        StepVerifier.create(PassengerValidationOperations.validateTripDependencies(trip, routeService, vehicleService))
                .expectErrorMatches(throwable -> throwable instanceof PassengerNotFoundException &&
                        throwable.getMessage().contains("Route not found for ID: " + trip.getRouteId()))
                .verify();
    }

    @Test
    @DisplayName("Test passengerNotFound - Throws PassengerNotFoundException")
    void testPassengerNotFound() {
        String expectedPassengerId = "6734854cd7801b590fbd701a";
        StepVerifier.create(PassengerValidationOperations.passengerNotFound(expectedPassengerId))
                .expectErrorMatches(throwable -> throwable instanceof PassengerNotFoundException)
                .verify();
    }


    @Test
    @DisplayName("Test tripNotFound - Throws TripNotFoundException")
    void testTripNotFound() {
        String expectedTripId = trip.getTripId();
        StepVerifier.create(PassengerValidationOperations.tripNotFound(expectedTripId))
                .expectErrorMatches(throwable -> throwable instanceof TripNotFoundException &&
                        throwable.getMessage().equals("Trip not found with ID: " + expectedTripId))
                .verify();
    }

}
