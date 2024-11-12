package com.workshop.passenger.application.exceptions;

import com.workshop.passenger.domain.exception.PassengerNotFoundException;
import com.workshop.passenger.domain.exception.TripNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.WebRequest;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

class GlobalExceptionHandlerTest {

    @Mock
    private WebRequest webRequest;

    @Mock
    private BindingResult bindingResult;

    @InjectMocks
    private GlobalExceptionHandler globalExceptionHandler;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testHandlePassengerNotFoundException() {
        PassengerNotFoundException ex = new PassengerNotFoundException("Passenger not found");
        Mono<ResponseEntity<ErrorResponse>> responseMono = globalExceptionHandler.handlePassengerNotFoundException(ex);
        ResponseEntity<ErrorResponse> response = responseMono.block();

        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatusCodeValue());
        assertEquals("PASSENGER_NOT_FOUND", response.getBody().getErrorCode());
    }

    @Test
    void testHandleTripNotFoundException() {
        TripNotFoundException ex = new TripNotFoundException("Trip not found");
        Mono<ResponseEntity<ErrorResponse>> responseMono = globalExceptionHandler.handleTripNotFoundException(ex);
        ResponseEntity<ErrorResponse> response = responseMono.block();

        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatusCodeValue());
        assertEquals("TRIP_NOT_FOUND", response.getBody().getErrorCode());
    }

    @Test
    void testHandleIllegalArgumentException() {
        IllegalArgumentException ex = new IllegalArgumentException("Invalid input data");
        Mono<ResponseEntity<ErrorResponse>> responseMono = globalExceptionHandler.handleIllegalArgument(ex, webRequest);
        ResponseEntity<ErrorResponse> response = responseMono.block();

        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCodeValue());
        assertEquals("Invalid input: Invalid input data", response.getBody().getMessage());
        assertEquals("INVALID_ARGUMENT", response.getBody().getErrorCode());
    }

    @Test
    void testHandleHttpMessageConversionException() {
        HttpMessageConversionException ex = new HttpMessageConversionException("Conversion error");
        Mono<ResponseEntity<ErrorResponse>> responseMono = globalExceptionHandler.handleHttpMessageConversionException(ex, webRequest);
        ResponseEntity<ErrorResponse> response = responseMono.block();

        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCodeValue());
        assertEquals("Invalid JSON format: Conversion error", response.getBody().getMessage());
        assertEquals("INVALID_JSON", response.getBody().getErrorCode());
    }

    @Test
    void testHandleNumberFormatException() {
        NumberFormatException ex = new NumberFormatException("Invalid number format");
        Mono<ResponseEntity<ErrorResponse>> responseMono = globalExceptionHandler.handleNumberFormatException(ex, webRequest);
        ResponseEntity<ErrorResponse> response = responseMono.block();

        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCodeValue());
        assertEquals("Invalid number format", response.getBody().getMessage());
        assertEquals("NUMBER_FORMAT_EXCEPTION", response.getBody().getErrorCode());
    }

    @Test
    void testHandleMethodArgumentNotValidException() {
        FieldError fieldError = new FieldError("objectName", "fieldName", "Invalid field");
        when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError));

        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(null, bindingResult);
        Mono<ResponseEntity<ErrorResponse>> responseMono = globalExceptionHandler.handleMethodArgumentNotValid(ex);
        ResponseEntity<ErrorResponse> response = responseMono.block();

        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCodeValue());
        assertEquals("Validation failed: fieldName - Invalid field; ", response.getBody().getMessage());
        assertEquals("VALIDATION_ERROR", response.getBody().getErrorCode());
    }

    @Test
    void testHandleNoSuchElementException() {
        NoSuchElementException ex = new NoSuchElementException("Element not found");
        Mono<ResponseEntity<ErrorResponse>> responseMono = globalExceptionHandler.handleNoSuchElementException(ex, webRequest);
        ResponseEntity<ErrorResponse> response = responseMono.block();

        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatusCodeValue());
        assertEquals("Element not found", response.getBody().getMessage());
        assertEquals("ELEMENT_NOT_FOUND", response.getBody().getErrorCode());
    }

    @Test
    void testHandleGeneralException() {
        Exception ex = new Exception("General error");
        Mono<ResponseEntity<ErrorResponse>> responseMono = globalExceptionHandler.handleGeneralException(ex, webRequest);
        ResponseEntity<ErrorResponse> response = responseMono.block();

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), response.getStatusCodeValue());
        assertEquals("An unexpected error occurred: General error", response.getBody().getMessage());
        assertEquals("INTERNAL_SERVER_ERROR", response.getBody().getErrorCode());
    }
}
