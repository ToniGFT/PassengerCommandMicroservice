package com.workshop.passenger;

import com.workshop.passenger.infraestructure.Route.service.RouteService;
import com.workshop.passenger.infraestructure.Vehicle.service.VehicleService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest
class PassengerApplicationTests {

    @MockBean
    private RouteService routeService;

    @MockBean
    private VehicleService vehicleService;

    @Test
    void contextLoads() {
    }

}

