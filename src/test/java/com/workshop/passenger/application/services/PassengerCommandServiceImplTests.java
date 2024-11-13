package com.workshop.passenger.application.services;

import com.workshop.passenger.application.dto.PassengerUpdateDTO;
import com.workshop.passenger.domain.exception.PassengerNotFoundException;
import com.workshop.passenger.domain.model.aggregates.Passenger;
import com.workshop.passenger.domain.model.entities.Trip;
import com.workshop.passenger.domain.repository.PassengerCommandRepository;
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
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DisplayName("PassengerCommandServiceImpl Unit Tests")
class PassengerCommandServiceImplTests {

    @InjectMocks
    private PassengerCommandServiceImpl passengerService;

    @Mock
    private PassengerCommandRepository passengerCommandRepository;

    @Mock
    private RouteService routeService;

    @Mock
    private VehicleService vehicleService;

    private Passenger passenger;
    private PassengerUpdateDTO passengerUpdateDTO;
    private Trip trip;
    private Route route;
    private Vehicle vehicle;
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

        passengerUpdateDTO = PassengerUpdateDTO.builder()
                .name("Jane Doe")
                .email("jane.doe@example.com")
                .phone("987-654-3210")
                .preferredPaymentMethod("Debit Card")
                .build();

        trip = Trip.builder()
                .tripId(tripId)
                .routeId("route123")
                .vehicleId("vehicle123")
                .startStop("A")
                .endStop("B")
                .fare(10.0)
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


    // Happy Path Tests
    @Test
    @DisplayName("Test createPassenger - Successful Save")
    void testCreatePassenger_Success() {
        when(passengerCommandRepository.save(any(Passenger.class))).thenReturn(Mono.just(passenger));
        when(routeService.getRouteById(anyString())).thenReturn(Mono.just(route));
        when(vehicleService.getVehicleById(anyString())).thenReturn(Mono.just(mock(Vehicle.class)));

        Mono<Passenger> result = passengerService.createPassenger(passenger);

        StepVerifier.create(result)
                .expectNextMatches(savedPassenger -> savedPassenger.getName().equals("John Doe"))
                .verifyComplete();

        verify(passengerCommandRepository, times(1)).save(any(Passenger.class));
        verify(routeService, times(passenger.getTrips().size())).getRouteById(anyString());
        verify(vehicleService, times(passenger.getTrips().size())).getVehicleById(anyString());
    }

    @Test
    @DisplayName("Test updatePassenger - Successful Update")
    void testUpdatePassenger_Success() {
        when(passengerCommandRepository.findById(passengerId)).thenReturn(Mono.just(passenger));
        when(passengerCommandRepository.save(any(Passenger.class))).thenReturn(Mono.just(passenger));
        when(routeService.getRouteById(anyString())).thenReturn(Mono.just(route));
        when(vehicleService.getVehicleById(anyString())).thenReturn(Mono.just(mock(Vehicle.class)));

        Mono<Passenger> result = passengerService.updatePassenger(passengerId, passengerUpdateDTO);

        StepVerifier.create(result)
                .expectNextMatches(updated -> updated.getName().equals("Jane Doe"))
                .verifyComplete();

        verify(passengerCommandRepository, times(1)).findById(passengerId);
        verify(passengerCommandRepository, times(1)).save(any(Passenger.class));
        verify(routeService, times(passenger.getTrips().size())).getRouteById(anyString());
        verify(vehicleService, times(passenger.getTrips().size())).getVehicleById(anyString());
    }

    @Test
    @DisplayName("Test deletePassenger - Successful Deletion")
    void testDeletePassenger_Success() {
        when(passengerCommandRepository.findById(passengerId)).thenReturn(Mono.just(passenger));
        when(passengerCommandRepository.deleteById(passengerId)).thenReturn(Mono.empty());

        Mono<Void> result = passengerService.deletePassenger(passengerId);

        StepVerifier.create(result)
                .verifyComplete();

        verify(passengerCommandRepository, times(1)).findById(passengerId);
        verify(passengerCommandRepository, times(1)).deleteById(passengerId);
    }

    @Test
    @DisplayName("Test addTripToPassenger - Trip Added Successfully with Valid Dependencies")
    void testAddTripToPassenger_Success() {
        when(routeService.getRouteById(trip.getRouteId())).thenReturn(Mono.just(route)); // Puedes usar un objeto simulado si `Route` es otra clase específica
        when(vehicleService.getVehicleById(trip.getVehicleId())).thenReturn(Mono.just(new Vehicle()));
        when(passengerCommandRepository.findById(passengerId)).thenReturn(Mono.just(passenger));
        when(passengerCommandRepository.save(any(Passenger.class))).thenReturn(Mono.just(passenger));

        Mono<Passenger> result = passengerService.addTripToPassenger(passengerId, trip);

        StepVerifier.create(result)
                .expectNextMatches(passengerWithTrip -> passengerWithTrip.getTrips().contains(trip))
                .verifyComplete();

        verify(routeService, times(1)).getRouteById(trip.getRouteId());
        verify(vehicleService, times(1)).getVehicleById(trip.getVehicleId());
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

    // Sad Path Tests
    @Test
    @DisplayName("Test addTripToPassenger - Passenger Not Found")
    void testAddTripToPassenger_PassengerNotFound() {
        when(passengerCommandRepository.findById(passengerId)).thenReturn(Mono.empty());

        Mono<Passenger> result = passengerService.addTripToPassenger(passengerId, trip);

        StepVerifier.create(result)
                .expectError(PassengerNotFoundException.class)
                .verify();

        verify(passengerCommandRepository, times(1)).findById(passengerId);
        verify(passengerCommandRepository, never()).save(any(Passenger.class));
    }

    @Test
    @DisplayName("Test addTripToPassenger - Missing Route or Vehicle")
    void testAddTripToPassenger_MissingDependencies() {
        when(routeService.getRouteById(trip.getRouteId())).thenReturn(Mono.empty());
        when(vehicleService.getVehicleById(trip.getVehicleId())).thenReturn(Mono.empty());
        when(passengerCommandRepository.findById(passengerId)).thenReturn(Mono.just(passenger));

        Mono<Passenger> result = passengerService.addTripToPassenger(passengerId, trip);

        StepVerifier.create(result)
                .expectError(PassengerNotFoundException.class)
                .verify();

        verify(routeService, times(1)).getRouteById(trip.getRouteId());
        verify(vehicleService, times(1)).getVehicleById(trip.getVehicleId());
        verify(passengerCommandRepository, times(1)).findById(passengerId);
        verify(passengerCommandRepository, never()).save(any(Passenger.class));
    }
}
