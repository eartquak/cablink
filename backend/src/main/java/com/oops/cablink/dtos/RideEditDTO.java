package com.oops.cablink.dtos;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Data;
import org.geojson.GeoJsonObject;

import java.time.LocalDateTime;

@Data
public class RideEditDTO {
    @Valid

    @NotBlank
    @NotEmpty
    String name;

    @Min(value = 2)
    int seats;

    @Positive
    double price;

    GeoJsonObject locationStart;

    GeoJsonObject locationEnd;

    @Future
    LocalDateTime dateTime;
}

