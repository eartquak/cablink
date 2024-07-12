package com.oops.cablink.controllers;

import com.oops.cablink.models.Ride;
import com.oops.cablink.models.User;
import com.oops.cablink.repositories.RideRepository;
import com.oops.cablink.repositories.UserRepository;
import com.oops.cablink.response.GenericResponse;
import com.oops.cablink.request.RideCreate;
import jakarta.validation.executable.ValidateOnExecution;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;

@RestController
public class RideController {

    @Autowired
    RideRepository rideRepository;

    @Autowired
    UserRepository userRepository;


    @PostMapping(path = "/ride/create")
    public ResponseEntity<GenericResponse> create(
            @AuthenticationPrincipal
            OAuth2User principal,
            @RequestBody
            @Validated
            RideCreate rideCreate
    ) {
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

        if (currentUser == null) {
            return new ResponseEntity<GenericResponse>(
                    new GenericResponse("Could not find user", GenericResponse.ResponseStatus.ERROR), HttpStatus.NOT_FOUND
            );
        }



        /*Ride ride = new Ride(new ObjectId(),
                "Varun's Ride",
                currentUser,
                new ArrayList<User>(),
                1000,
                4,
                0
        )*/
        Ride ride = new Ride(
                new ObjectId(),
                rideCreate.name(),
                currentUser,
                new ArrayList<User>(),
                rideCreate.price(),
                rideCreate.seats(),
                0,
                rideCreate.locationStart(),
                rideCreate.locationEnd(),
                rideCreate.dateTime()
        );

        ride = rideRepository.save(ride);
        return new ResponseEntity<GenericResponse>(
                new GenericResponse(ride,  GenericResponse.ResponseStatus.SUCCESS), HttpStatus.CREATED
        );
    }

    @ValidateOnExecution
    @GetMapping("/ride/all")
    public ResponseEntity<GenericResponse> getAllRides (
            @AuthenticationPrincipal
            OAuth2User principal
    ) {
        if (principal.getAttribute("email") == null || Objects.requireNonNull(principal.getAttribute("email")).toString().isEmpty() || Objects.requireNonNull(
                principal.getAttribute("email")).toString().isBlank()) {
            return new ResponseEntity<GenericResponse>(
                    new GenericResponse("Error", GenericResponse.ResponseStatus.ERROR),HttpStatus.UNAUTHORIZED
            );
        }

        final User currentUser = userRepository.findByEmail(Objects.requireNonNull(principal.getAttribute("email")).toString());
        if (currentUser == null) {
            return new ResponseEntity<GenericResponse>(new GenericResponse(
                    "Error",
                    GenericResponse.ResponseStatus.ERROR
            ), HttpStatus.UNAUTHORIZED);
        }

        return new ResponseEntity<GenericResponse>(
                new GenericResponse(rideRepository.findAll(), GenericResponse.ResponseStatus.SUCCESS), HttpStatus.OK
        );
    }

    @GetMapping("/ride/{id}")
    public ResponseEntity<GenericResponse> getRideDetails (
            @AuthenticationPrincipal
            OAuth2User principal,

            @PathVariable
            ObjectId id
    ) {
        if (principal.getAttribute("email") == null || Objects.requireNonNull(principal.getAttribute("email")).toString().isEmpty() || Objects.requireNonNull(
                principal.getAttribute("email")).toString().isBlank()) {
            return new ResponseEntity<GenericResponse>(
                    new GenericResponse("Error", GenericResponse.ResponseStatus.ERROR),HttpStatus.UNAUTHORIZED
            );
        }

        final User currentUser = userRepository.findByEmail(Objects.requireNonNull(principal.getAttribute("email")).toString());
        if (currentUser == null) {
            return new ResponseEntity<GenericResponse>(new GenericResponse(
                    "Error",
                    GenericResponse.ResponseStatus.ERROR
            ), HttpStatus.UNAUTHORIZED);
        }

        final Optional<Ride> currentRideOptional = rideRepository.findById(id);
        return currentRideOptional.map(ride -> new ResponseEntity<GenericResponse>(
                new GenericResponse(ride, GenericResponse.ResponseStatus.SUCCESS), HttpStatus.OK
        )).orElseGet(() -> new ResponseEntity<GenericResponse>(
                new GenericResponse("No Ride with this ID", GenericResponse.ResponseStatus.ERROR), HttpStatus.UNAUTHORIZED)
        );
    }

    @GetMapping("/ride/add/{id}")
    public ResponseEntity<GenericResponse> addToRide (
            @AuthenticationPrincipal
            OAuth2User principal,

            @PathVariable
            ObjectId id
    ) {
        if (principal.getAttribute("email") == null || Objects.requireNonNull(principal.getAttribute("email")).toString().isEmpty() || Objects.requireNonNull(
                principal.getAttribute("email")).toString().isBlank()) {
            return new ResponseEntity<GenericResponse>(
                    new GenericResponse("Error", GenericResponse.ResponseStatus.ERROR),HttpStatus.UNAUTHORIZED
            );
        }

        final User currentUser = userRepository.findByEmail(Objects.requireNonNull(principal.getAttribute("email")).toString());
        if (currentUser == null) {
            return new ResponseEntity<GenericResponse>(new GenericResponse(
                    "Error",
                    GenericResponse.ResponseStatus.ERROR
            ), HttpStatus.UNAUTHORIZED);
        }

        final Optional<Ride> currentRideOptional = rideRepository.findById(id);
        if (currentRideOptional.isEmpty()) {
            return new ResponseEntity<GenericResponse>(
                    new GenericResponse("No Ride with this ID", GenericResponse.ResponseStatus.ERROR), HttpStatus.UNAUTHORIZED
            );
        }
        Ride currentRide = currentRideOptional.get();
        currentRide.getRiders().add(currentUser);
        currentRide.setSeatsFilled(currentRide.getSeatsFilled()+1);

        if (currentUser.hashCode() == currentRide.getHost().hashCode()) {
            return new ResponseEntity<GenericResponse>(
                    new GenericResponse("Host and Rider cannot be same", GenericResponse.ResponseStatus.ERROR), HttpStatus.UNAUTHORIZED
            );
        }


        return new ResponseEntity<GenericResponse>(
                new GenericResponse(rideRepository.save(currentRide), GenericResponse.ResponseStatus.SUCCESS), HttpStatus.OK
        );
    }


    @GetMapping("/ride/delete/{id}")
    public ResponseEntity<GenericResponse> deleteRide (
            @AuthenticationPrincipal
            OAuth2User principal,

            @PathVariable
            ObjectId id
    ) {
        if (principal.getAttribute("email") == null || Objects.requireNonNull(principal.getAttribute("email")).toString().isEmpty() || Objects.requireNonNull(
                principal.getAttribute("email")).toString().isBlank()) {
            return new ResponseEntity<GenericResponse>(
                    new GenericResponse("Error", GenericResponse.ResponseStatus.ERROR),HttpStatus.UNAUTHORIZED
            );
        }

        final User currentUser = userRepository.findByEmail(Objects.requireNonNull(principal.getAttribute("email")).toString());
        if (currentUser == null) {
            return new ResponseEntity<GenericResponse>(new GenericResponse(
                    "Error",
                    GenericResponse.ResponseStatus.ERROR
            ), HttpStatus.UNAUTHORIZED);
        }


        final Optional<Ride> currentRideOptional = rideRepository.findById(id);
        if (currentRideOptional.isEmpty()) {
            return new ResponseEntity<GenericResponse>(
                    new GenericResponse("No Ride with this ID", GenericResponse.ResponseStatus.ERROR), HttpStatus.UNAUTHORIZED
            );
        }
        else {
            Ride currentRide = currentRideOptional.get();
            if (currentUser.hashCode() != currentRide.getHost().hashCode()) {
                return new ResponseEntity<GenericResponse>(
                        new GenericResponse("Not Authorized to delete", GenericResponse.ResponseStatus.ERROR), HttpStatus.UNAUTHORIZED
                );
            }

            rideRepository.deleteById(id);
            return new ResponseEntity<GenericResponse>(
                    new GenericResponse(currentRide, GenericResponse.ResponseStatus.SUCCESS), HttpStatus.OK
            );
        }
    }
}
