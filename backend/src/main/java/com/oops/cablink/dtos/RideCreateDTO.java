package com.oops.cablink.dtos;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Data;
import org.geojson.GeoJsonObject;

import java.time.LocalDateTime;

@Data
public class RideCreateDTO {
        @Valid

        @NotEmpty
        @NotBlank
        @NotNull
        String name;

        @Min(value = 2)
        @NotNull
        int seats;

        @Positive
        @NotNull
        double price;

        @NotNull
        GeoJsonObject locationStart;

        @NotNull
        GeoJsonObject locationEnd;

        @NotNull
        @Future
        LocalDateTime dateTime;
}