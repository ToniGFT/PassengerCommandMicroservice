package com.workshop.passenger.application.exceptions;

import com.workshop.passenger.domain.exception.PassengerNotFoundException;
import com.workshop.passenger.domain.exception.TripNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.NoSuchElementException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(PassengerNotFoundException.class)
    public Mono<ResponseEntity<ErrorResponse>> handlePassengerNotFoundException(PassengerNotFoundException ex) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .message(ex.getMessage())
                .errorCode("PASSENGER_NOT_FOUND")
                .status(HttpStatus.NOT_FOUND)
                .build();
        return Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse));
    }

    @ExceptionHandler(TripNotFoundException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleTripNotFoundException(TripNotFoundException ex) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .message(ex.getMessage())
                .errorCode("TRIP_NOT_FOUND")
                .status(HttpStatus.NOT_FOUND)
                .build();
        return Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleIllegalArgument(IllegalArgumentException e, WebRequest request) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .message("Invalid input: " + e.getMessage())
                .errorCode("INVALID_ARGUMENT")
                .status(HttpStatus.BAD_REQUEST)
                .build();
        return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse));
    }

    @ExceptionHandler(HttpMessageConversionException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleHttpMessageConversionException(HttpMessageConversionException e, WebRequest request) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .message("Invalid JSON format: " + e.getMessage())
                .errorCode("INVALID_JSON")
                .status(HttpStatus.BAD_REQUEST)
                .build();
        return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse));
    }

    @ExceptionHandler(NumberFormatException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleNumberFormatException(NumberFormatException e, WebRequest request) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .message("Invalid number format")
                .errorCode("NUMBER_FORMAT_EXCEPTION")
                .status(HttpStatus.BAD_REQUEST)
                .build();
        return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        BindingResult result = ex.getBindingResult();
        List<FieldError> fieldErrors = result.getFieldErrors();
        StringBuilder errorMessage = new StringBuilder("Validation failed: ");
        for (FieldError fieldError : fieldErrors) {
            errorMessage.append(fieldError.getField()).append(" - ").append(fieldError.getDefaultMessage()).append("; ");
        }
        ErrorResponse errorResponse = ErrorResponse.builder()
                .message(errorMessage.toString())
                .errorCode("VALIDATION_ERROR")
                .status(HttpStatus.BAD_REQUEST)
                .build();
        return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse));
    }

    @ExceptionHandler(NoSuchElementException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleNoSuchElementException(NoSuchElementException e, WebRequest request) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .message(e.getMessage())
                .errorCode("ELEMENT_NOT_FOUND")
                .status(HttpStatus.NOT_FOUND)
                .build();
        return Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse));
    }

    @ExceptionHandler(Exception.class)
    public Mono<ResponseEntity<ErrorResponse>> handleGeneralException(Exception e, WebRequest request) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .message("An unexpected error occurred: " + e.getMessage())
                .errorCode("INTERNAL_SERVER_ERROR")
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .build();
        return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse));
    }
}
