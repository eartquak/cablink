package com.oops.cablink.dtos;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Data;
import org.geojson.GeoJsonObject;

import java.time.LocalDateTime;

@Data
public class RideCreateDTO {
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

        @Future
        LocalDateTime dateTime;
}