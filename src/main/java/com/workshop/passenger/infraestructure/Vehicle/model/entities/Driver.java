package com.workshop.passenger.infraestructure.Vehicle.model.entities;

import com.workshop.passenger.infraestructure.Vehicle.model.valueobjects.Contact;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Driver {

    private ObjectId driverId;

    @NotBlank(message = "Driver name cannot be empty")
    private String name;

    @Valid
    private Contact contact;
}
