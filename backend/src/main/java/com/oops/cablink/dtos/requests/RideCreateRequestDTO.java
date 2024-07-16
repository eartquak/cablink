package com.oops.cablink.dtos.requests;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Data;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;

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
        GeoJsonPoint locationStart;

        @NotNull
        GeoJsonPoint locationEnd;

        @NotNull
        @Future
        LocalDateTime dateTime;
}