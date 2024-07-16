package com.oops.cablink.models;


import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.bson.types.ObjectId;
import org.geojson.GeoJsonObject;
import org.hibernate.validator.constraints.UniqueElements;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

import java.time.LocalDateTime;
import java.util.ArrayList;

@Document(collection = "rides")
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Ride {

    @Id
    private ObjectId id;

    @NotNull
    @NotBlank
    private String name;

    @NotNull
    @DocumentReference
    private User host;

    @UniqueElements
    @DocumentReference
    private ArrayList<User> riders;

    @Positive
    private double price;

    @Min(value = 2)
    private int seats;

    @Min(value = 1)
    private int seatsFilled;

    @NotNull
    private GeoJsonObject locationStart;

    @NotNull
    private GeoJsonObject locationEnd;

    @NotNull
    private LocalDateTime dateTime;

    @NotNull
    private Boolean active;

}
