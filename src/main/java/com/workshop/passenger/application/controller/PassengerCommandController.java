package com.workshop.passenger.application.controller;

import com.workshop.passenger.application.response.service.PassengerResponseService;
import com.workshop.passenger.application.services.PassengerCommandService;
import com.workshop.passenger.domain.model.aggregates.Passenger;
import com.workshop.passenger.domain.model.entities.Trip;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

@RestController
@RequestMapping("/passengers")
public class PassengerCommandController {

    private static final Logger logger = LoggerFactory.getLogger(PassengerCommandController.class);
    private final PassengerCommandService passengerCommandService;
    private final PassengerResponseService passengerResponseService;

    public PassengerCommandController(PassengerCommandService passengerCommandService, PassengerResponseService passengerResponseService) {
        this.passengerCommandService = passengerCommandService;
        this.passengerResponseService = passengerResponseService;
    }

    @PostMapping
    public Mono<ResponseEntity<Passenger>> createPassenger(@Valid @RequestBody Passenger passenger) {
        logger.info("Attempting to create a new passenger with name: {}", passenger.getName());
        return passengerCommandService.createPassenger(passenger)
                .flatMap(passengerResponseService::buildCreatedResponse)
                .doOnSuccess(response -> logger.info("Successfully created passenger with NAME: {}", response.getBody().getName()));
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<Passenger>> updatePassenger(
            @PathVariable String id,
            @Valid @RequestBody Passenger updatedPassenger) {
        logger.info("Attempting to update passenger with ID: {}", id);
        return passengerCommandService.updatePassenger(id, updatedPassenger)
                .flatMap(passengerResponseService::buildOkResponse)
                .doOnSuccess(response -> logger.info("Successfully updated passenger with ID: {}", id));
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deletePassenger(@PathVariable String id) {
        logger.info("Attempting to delete passenger with ID: {}", id);
        return passengerCommandService.deletePassenger(id)
                .then(passengerResponseService.buildNoContentResponse())
                .doOnSuccess(response -> logger.info("Successfully deleted passenger with ID: {}", id));
    }

    @PostMapping("/{id}/trips")
    public Mono<ResponseEntity<Passenger>> addTripToPassenger(
            @PathVariable String id,
            @Valid @RequestBody Trip trip) {
        logger.info("Attempting to add a trip to passenger with ID: {}", id);
        return passengerCommandService.addTripToPassenger(id, trip)
                .flatMap(passengerResponseService::buildOkResponse)
                .doOnSuccess(response -> logger.info("Successfully added trip with ID: {} to passenger with ID: {}", trip.getTripId(), id));
    }

    @DeleteMapping("/{passengerId}/trips/{tripId}")
    public Mono<ResponseEntity<Passenger>> removeTripFromPassenger(
            @PathVariable String passengerId,
            @PathVariable String tripId) {
        logger.info("Attempting to remove trip with ID: {} from passenger with ID: {}", tripId, passengerId);
        return passengerCommandService.removeTripFromPassenger(passengerId, tripId)
                .flatMap(passengerResponseService::buildOkResponse)
                .doOnSuccess(response -> logger.info("Successfully removed trip with ID: {} from passenger with ID: {}", tripId, passengerId));
    }
}
