package com.oops.cablink.controllers;

import com.oops.cablink.services.UserService;
import com.oops.cablink.models.Ride;
import com.oops.cablink.models.User;
import com.oops.cablink.repositories.RideRepository;
import com.oops.cablink.response.GenericResponse;
import com.oops.cablink.dtos.RideCreateDTO;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Optional;

@RestController
public class RideController {

    @Autowired
    RideRepository rideRepository;

    @Autowired
    UserService userService;


    @PostMapping("/ride/create")
    public ResponseEntity<GenericResponse> create (
            @AuthenticationPrincipal
            OAuth2User principal,

            @RequestBody
            RideCreateDTO rideCreateDTO
    ) {
        GenericResponse userResponse = userService.getUser(principal);
        if (userResponse.httpStatus != HttpStatus.OK) {
            return new ResponseEntity<GenericResponse>(userResponse, userResponse.httpStatus);
        }

        final User currentUser = (User)userResponse.data;

        Ride ride = new Ride(
                new ObjectId(),
                rideCreateDTO.getName(),
                currentUser,
                new ArrayList<User>(),
                rideCreateDTO.getPrice(),
                rideCreateDTO.getSeats(),
                1,
                rideCreateDTO.getLocationStart(),
                rideCreateDTO.getLocationEnd(),
                rideCreateDTO.getDateTime(),
                true
        );

        ride.getRiders().add(currentUser);
        ride = rideRepository.save(ride).block();
        return new ResponseEntity<GenericResponse>(
                new GenericResponse(ride,  HttpStatus.CREATED), HttpStatus.CREATED
        );
    }

    @GetMapping("/ride/all")
    public ResponseEntity<GenericResponse> getAllRides (
            @AuthenticationPrincipal
            OAuth2User principal
    ) {

        GenericResponse userResponse = userService.getUser(principal);
        if (userResponse.httpStatus != HttpStatus.OK) {
            return new ResponseEntity<GenericResponse>(userResponse, userResponse.httpStatus);
        }
        final User currentUser = (User)userResponse.data;

        return new ResponseEntity<GenericResponse>(
                new GenericResponse(rideRepository.findAll().collectList().block(), HttpStatus.OK), HttpStatus.OK
        );
    }

    /*
    @GetMapping("/ride/filtered")
    public ResponseEntity<GenericResponse> getFilteredRides (
            @AuthenticationPrincipal
            OAuth2User principal
    ) {
        GenericResponse userResponse = userService.getUser(principal);
        if (userResponse.httpStatus != HttpStatus.OK) {
            return new ResponseEntity<GenericResponse>(userResponse, userResponse.httpStatus);
        }
        final User currentUser = (User)userResponse.data;


    }
     */

    @GetMapping("/ride/{id}")
    public ResponseEntity<GenericResponse> getRideDetails (
            @AuthenticationPrincipal
            OAuth2User principal,

            @PathVariable
            ObjectId id
    ) {

        GenericResponse userResponse = userService.getUser(principal);
        if (userResponse.httpStatus != HttpStatus.OK) {
            return new ResponseEntity<GenericResponse>(userResponse, userResponse.httpStatus);
        }

        final User currentUser = (User)userResponse.data;

        final Optional<Ride> currentRideOptional = rideRepository.findById(id).blockOptional();
        return currentRideOptional.map(ride -> new ResponseEntity<GenericResponse>(
                new GenericResponse(ride, HttpStatus.OK), HttpStatus.OK
        )).orElseGet(() -> new ResponseEntity<GenericResponse>(
                new GenericResponse("No Ride with this ID", HttpStatus.NOT_FOUND), HttpStatus.NOT_FOUND)
        );
    }

    @GetMapping("/ride/add/{id}")
    public ResponseEntity<GenericResponse> addToRide (
            @AuthenticationPrincipal
            OAuth2User principal,

            @PathVariable
            ObjectId id
    ) {

        GenericResponse userResponse = userService.getUser(principal);
        if (userResponse.httpStatus != HttpStatus.OK) {
            return new ResponseEntity<GenericResponse>(userResponse, userResponse.httpStatus);
        }

        final User currentUser = (User)userResponse.data;

        final Optional<Ride> currentRideOptional = rideRepository.findById(id).blockOptional();
        if (currentRideOptional.isEmpty()) {
            return new ResponseEntity<GenericResponse>(
                    new GenericResponse("No Ride with this ID", HttpStatus.NOT_FOUND), HttpStatus.NOT_FOUND
            );
        }
        Ride currentRide = currentRideOptional.get();
        currentRide.getRiders().add(currentUser);
        currentRide.setSeatsFilled(currentRide.getSeatsFilled()+1);

        if (currentUser.hashCode() == currentRide.getHost().hashCode()) {
            return new ResponseEntity<GenericResponse>(
                    new GenericResponse("Host and Rider cannot be same", HttpStatus.FORBIDDEN), HttpStatus.FORBIDDEN
            );
        }


        return new ResponseEntity<GenericResponse>(
                new GenericResponse(rideRepository.save(currentRide).block(), HttpStatus.OK), HttpStatus.OK
        );
    }


    @GetMapping("/ride/delete/{id}")
    public ResponseEntity<GenericResponse> deleteRide (
            @AuthenticationPrincipal
            OAuth2User principal,

            @PathVariable
            ObjectId id
    ) {

        GenericResponse userResponse = userService.getUser(principal);
        if (userResponse.httpStatus != HttpStatus.OK) {
            return new ResponseEntity<GenericResponse>(userResponse, userResponse.httpStatus);
        }

        final User currentUser = (User)userResponse.data;


        final Optional<Ride> currentRideOptional = rideRepository.findById(id).blockOptional();
        if (currentRideOptional.isEmpty()) {
            return new ResponseEntity<GenericResponse>(
                    new GenericResponse("No Ride with this ID", HttpStatus.NOT_FOUND), HttpStatus.NOT_FOUND
            );
        }
        else {
            Ride currentRide = currentRideOptional.get();
            if (currentUser.hashCode() != currentRide.getHost().hashCode()) {
                return new ResponseEntity<GenericResponse>(
                        new GenericResponse("Not Authorized to delete", HttpStatus.FORBIDDEN), HttpStatus.FORBIDDEN
                );
            }

            rideRepository.deleteById(id).block();
            return new ResponseEntity<GenericResponse>(
                    new GenericResponse(currentRide, HttpStatus.OK), HttpStatus.OK
            );
        }
    }
}
