package com.oops.cablink.dtos.requests;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Data;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;

import java.time.LocalDateTime;

@Data
public class RideEditRequestDTO {
    @Valid

    @NotBlank
    @NotEmpty
    String name;

    @Min(value = 2)
    int seats;

    @Positive
    double price;

    GeoJsonPoint locationStart;

    GeoJsonPoint locationEnd;

    @Future
    LocalDateTime dateTime;
}

