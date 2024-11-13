package com.workshop.passenger.infrastructure.route.service;

import com.workshop.passenger.infraestructure.Route.model.aggregates.Route;
import com.workshop.passenger.infraestructure.Route.service.RouteService;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.*;

class RouteServiceTest {

    private MockWebServer mockWebServer;
    private RouteService routeService;
    private WebClient webClient;

    @BeforeEach
    void setUp() throws Exception {
        mockWebServer = new MockWebServer();
        mockWebServer.start();

        webClient = WebClient.create(mockWebServer.url("/").toString());

        routeService = new RouteService(webClient,
                mockWebServer.url("/").toString(),
                "/routes/{id}");
    }

    @Test
    @DisplayName("When fetching a route by ID, then the correct route is returned")
    void testGetRouteById() {
        String routeJson = """
                {
                    "_id": "672caeb97e634e3fe18ada37",
                    "routeName": "Route 1",
                    "stops": [
                        {
                            "stopId": "stop123",
                            "stopName": "Stop A",
                            "coordinates": {
                                "latitude": 37.7749,
                                "longitude": -122.4194
                            },
                            "arrivalTimes": ["08:00", "09:00"]
                        }
                    ],
                    "schedule": {
                        "weekdays": {
                            "startTime": "08:00",
                            "endTime": "22:00",
                            "frequencyMinutes": 15
                        },
                        "weekends": {
                            "startTime": "09:00",
                            "endTime": "20:00",
                            "frequencyMinutes": 20
                        }
                    }
                }
                """;

        mockWebServer.enqueue(new MockResponse()
                .setBody(routeJson)
                .addHeader("Content-Type", "application/json"));

        Mono<Route> routeMono = routeService.getRouteById("1");
        Route route = routeMono.block();

        assertNotNull(route);
        assertEquals("Route 1", route.getRouteName());
        assertEquals("stop123", route.getStops().get(0).getStopId());
    }

    @Test
    @DisplayName("When fetching a non-existent route by ID, then an empty result is returned")
    void testGetRouteByIdNotFound() {
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(404)
                .setBody("Not Found")
                .addHeader("Content-Type", "application/json"));

        Mono<Route> routeMono = routeService.getRouteById("999");
        Route route = routeMono.block();

        assertNull(route);
    }

    @Test
    @DisplayName("When fetching a route by ID and a server error occurs, then an empty result is returned")
    void testGetRouteByIdServerError() {
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(500)
                .setBody("Internal Server Error")
                .addHeader("Content-Type", "application/json"));

        Mono<Route> routeMono = routeService.getRouteById("1");
        Route route = routeMono.block();

        assertNull(route);
    }

    @AfterEach
    void tearDown() throws Exception {
        mockWebServer.shutdown();
    }
}
