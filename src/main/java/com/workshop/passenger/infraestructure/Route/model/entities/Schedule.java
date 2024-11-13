package com.workshop.passenger.infraestructure.Route.model.entities;

import com.workshop.passenger.infraestructure.Route.model.valueobjects.WeekSchedule;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Schedule {

    @NotNull(message = "El horario entre semana no puede ser nulo")
    @Valid
    private WeekSchedule weekdays;

    @NotNull(message = "El horario de fin de semana no puede ser nulo")
    @Valid
    private WeekSchedule weekends;

}

