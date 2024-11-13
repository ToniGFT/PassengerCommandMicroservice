package com.workshop.passenger.infraestructure.Route.service;

import com.workshop.passenger.infraestructure.Route.model.aggregates.Route;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class RouteService {

    private final WebClient webClient;
    private String baseUrl;
    private String routeUrl;

    @Autowired
    public RouteService(WebClient webClient,
                        @Value("${route.api.base-url}") String baseUrl,
                        @Value("${route.api.get-by-id}") String routeUrl) {
        this.webClient = webClient;
        this.routeUrl = routeUrl;
        this.baseUrl = baseUrl;
    }

    public Mono<Route> getRouteById(String idString) {
        return webClient.get()
                .uri(baseUrl + routeUrl, idString)
                .retrieve()
                .bodyToMono(Route.class)
                .onErrorResume(error -> {
                    System.err.println("Error calling Route microservice: " + error.getMessage());
                    return Mono.empty();
                });
    }

}
