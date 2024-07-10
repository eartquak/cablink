package com.example.cablink.request_model;

import jakarta.validation.constraints.*;
import org.geojson.GeoJsonObject;

import java.time.LocalDateTime;

public record RideCreate (

        @NotEmpty
        @NotBlank
        String name,

        @Min(value = 2)
        int seats,

        @Positive
        double price,

        @NotNull
        GeoJsonObject locationStart,

        @NotNull
        GeoJsonObject locationEnd,

        @NotNull
        LocalDateTime dateTime
) {

}