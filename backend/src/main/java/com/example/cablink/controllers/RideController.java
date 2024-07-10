package com.example.cablink.controllers;

import com.example.cablink.models.Ride;
import com.example.cablink.models.User;
import com.example.cablink.repositories.RideRepository;
import com.example.cablink.repositories.UserRepository;
import com.example.cablink.request_model.RideCreate;
import com.example.cablink.response.GenericResponse;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@RestController
public class RideController {

    @Autowired
    RideRepository rideRepository;

    @Autowired
    UserRepository userRepository;


    @PostMapping(path = "/user/create")
    public ResponseEntity<GenericResponse> create(
            @AuthenticationPrincipal
            OAuth2User principal,
            @RequestBody
            @Validated
            RideCreate rideCreate) {
        if (rideCreate == null) {
            return new ResponseEntity<GenericResponse>(
                    new GenericResponse( "Error", GenericResponse.ResponseStatus.ERROR), HttpStatus.UNAUTHORIZED
            );
        }
        if (principal.getAttribute("email") == null || Objects.requireNonNull(principal.getAttribute("email")).toString().isEmpty() || Objects.requireNonNull(
                principal.getAttribute("email")).toString().isBlank()) {
            return new ResponseEntity<GenericResponse>(
                    new GenericResponse("Error", GenericResponse.ResponseStatus.ERROR),HttpStatus.UNAUTHORIZED
            );
        }

        final User currentUser = userRepository.findByEmail(Objects.requireNonNull(principal.getAttribute("email")).toString());

        Ride ride = new Ride(
                new ObjectId(),
                rideCreate.name(),
                currentUser,
                null,
                rideCreate.price(),
                rideCreate.seats(),
                0,
                rideCreate.locationStart(),
                rideCreate.locationEnd(),
                rideCreate.dateTime()
        );
        return new ResponseEntity<GenericResponse>(
                new GenericResponse( rideRepository.save(ride),  GenericResponse.ResponseStatus.SUCCESS), HttpStatus.CREATED
        );
    }
}
