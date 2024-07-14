package com.oops.cablink.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Data;
import org.geojson.GeoJsonObject;

import java.time.LocalDateTime;

@Data
public class RideCreate {
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