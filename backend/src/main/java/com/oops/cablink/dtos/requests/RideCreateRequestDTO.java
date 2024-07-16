package com.oops.cablink.dtos.requests;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Data;
import org.geojson.GeoJsonObject;

import java.time.LocalDateTime;

@Data
public class RideCreateRequestDTO {
        @Valid

        @NotNull
        @NotBlank
        @NotEmpty
        String name;

        @NotNull
        @Min(value = 2)
        int seats;

        @NotNull
        @Positive
        double price;

        @NotNull
        GeoJsonObject locationStart;

        @NotNull
        GeoJsonObject locationEnd;

        @NotNull
        @Future
        LocalDateTime dateTime;
}