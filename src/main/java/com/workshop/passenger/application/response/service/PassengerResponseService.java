package com.workshop.passenger.application.response.service;

import com.workshop.passenger.application.response.builder.PassengerResponseBuilder;
import com.workshop.passenger.domain.model.aggregates.Passenger;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class PassengerResponseService {

    public Mono<ResponseEntity<Passenger>> buildCreatedResponse(Passenger passenger) {
        return Mono.fromCallable(() -> PassengerResponseBuilder.generateCreatedResponse(passenger));
    }

    public Mono<ResponseEntity<Passenger>> buildOkResponse(Passenger passenger) {
        return Mono.fromCallable(() -> PassengerResponseBuilder.generateOkResponse(passenger));
    }

    public Mono<ResponseEntity<Void>> buildNoContentResponse() {
        return Mono.just(PassengerResponseBuilder.generateNoContentResponse());
    }
}
