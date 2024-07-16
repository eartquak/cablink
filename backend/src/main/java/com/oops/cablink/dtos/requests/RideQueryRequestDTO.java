package com.oops.cablink.dtos.requests;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Data;
import org.geojson.GeoJsonObject;

import java.time.LocalDateTime;

@Data
public class RideQueryRequestDTO {
    @Valid

    @NotNull
    @Positive
    double price;

    @NotNull
    GeoJsonObject locationStart;

    @NotNull
    GeoJsonObject locationEnd;

    @NotNull
    LocalDateTime dateTime;
}
