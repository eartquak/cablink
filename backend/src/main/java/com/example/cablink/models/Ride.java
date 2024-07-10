package com.example.cablink.models;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.bson.types.ObjectId;
import org.geojson.GeoJsonObject;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;

@Document(collection = "rides")
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Ride {

    @Id
    private ObjectId id;

    private String name;

    @DocumentReference
    private User host;

    @DocumentReference
    private ArrayList<User> riders;

    private double price;
    private int seats;
    private int seatsFilled;
    private GeoJsonObject locationStart;
    private GeoJsonObject locationEnd;
    private LocalDateTime dateTime;

}
