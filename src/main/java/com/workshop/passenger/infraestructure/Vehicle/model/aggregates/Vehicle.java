package com.workshop.passenger.infraestructure.Vehicle.model.aggregates;

import com.workshop.passenger.infraestructure.Vehicle.model.entities.Driver;
import com.workshop.passenger.infraestructure.Vehicle.model.valueobjects.Coordinates;
import com.workshop.passenger.infraestructure.Vehicle.model.valueobjects.MaintenanceDetails;
import com.workshop.passenger.infraestructure.Vehicle.model.valueobjects.enums.VehicleStatus;
import com.workshop.passenger.infraestructure.Vehicle.model.valueobjects.enums.VehicleType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "Vehicle")
public class Vehicle {

    @Id
    private ObjectId vehicleId;

    @NotBlank(message = "License plate cannot be empty")
    private String licensePlate;

    @NotNull(message = "Capacity is required")
    @Min(value = 1, message = "Capacity must be at least 1")
    private Integer capacity;

    @NotNull(message = "Current status is required")
    private VehicleStatus currentStatus;

    @NotNull(message = "Vehicle type is required")
    private VehicleType type;

    @NotNull(message = "Driver is required")
    private Driver driver;

    @NotNull(message = "Maintenance details are required")
    private MaintenanceDetails maintenanceDetails;

    @NotNull(message = "Current location is required")
    private Coordinates currentLocation;

    private LocalDate lastMaintenance;

    @NotNull(message = "Route ID is required")
    private ObjectId routeId;
}
