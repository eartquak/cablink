package com.oops.cablink.dtos.responses;

import com.oops.cablink.models.Ride;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RideDetailsResponseDTO {

    Ride ride;
    boolean isUserInRide;
    boolean isUserHost;

}
