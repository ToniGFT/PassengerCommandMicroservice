package com.workshop.passenger.domain.model.aggregates;

import com.workshop.passenger.domain.model.entities.Trip;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;
import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "Passenger")
public class Passenger {

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Phone number is required")
    private String phone;

    @NotBlank(message = "Preferred payment method is required")
    private String preferredPaymentMethod;

    @NotNull(message = "Registration date is required")
    private LocalDateTime registeredAt;

    @NotEmpty(message = "Passenger must have at least one trip")
    @Valid
    private List<Trip> trips;

}