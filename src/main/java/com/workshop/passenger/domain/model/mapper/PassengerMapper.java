package com.workshop.passenger.domain.model.mapper;

import com.workshop.passenger.application.dto.PassengerUpdateDTO;
import com.workshop.passenger.domain.model.aggregates.Passenger;
import org.modelmapper.ModelMapper;

public class PassengerMapper {

    private static final ModelMapper modelMapper = new ModelMapper();

    private PassengerMapper() {
    }

    public static void mapToExistingPassenger(PassengerUpdateDTO source, Passenger target) {
        modelMapper.map(source, target);
    }
}
