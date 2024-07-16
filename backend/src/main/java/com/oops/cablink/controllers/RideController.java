package com.oops.cablink.controllers;

import com.oops.cablink.services.UserService;
import com.oops.cablink.models.Ride;
import com.oops.cablink.models.User;
import com.oops.cablink.repositories.RideRepository;
import com.oops.cablink.response.GenericResponse;
import com.oops.cablink.dtos.RideCreateDTO;
import jakarta.validation.Valid;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
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

            @Valid
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
        ride = rideRepository.save(ride);
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
                new GenericResponse(rideRepository.findAll(), HttpStatus.OK), HttpStatus.OK
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

        final Optional<Ride> currentRideOptional = rideRepository.findById(id);
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

        final Optional<Ride> currentRideOptional = rideRepository.findById(id);
        if (currentRideOptional.isEmpty()) {
            return new ResponseEntity<GenericResponse>(
                    new GenericResponse("No Ride with this ID", HttpStatus.NOT_FOUND), HttpStatus.NOT_FOUND
            );
        }
        Ride currentRide = currentRideOptional.get();
        currentRide.getRiders().add(currentUser);
        currentRide.setSeatsFilled(currentRide.getSeatsFilled()+1);

        if (rideRepository.isUserInRide(id, currentUser.getId()).isPresent()) {
            return new ResponseEntity<GenericResponse>(
                    new GenericResponse("Rider already in the Ride", HttpStatus.CONFLICT), HttpStatus.CONFLICT
            );
        }


        return new ResponseEntity<GenericResponse>(
                new GenericResponse(rideRepository.save(currentRide), HttpStatus.OK), HttpStatus.OK
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


        final Optional<Ride> currentRideOptional = rideRepository.findById(id);
        if (currentRideOptional.isEmpty()) {
            return new ResponseEntity<GenericResponse>(
                    new GenericResponse("No Ride with this ID", HttpStatus.NOT_FOUND), HttpStatus.NOT_FOUND
            );
        }

        Ride currentRide = currentRideOptional.get();
        if (rideRepository.isUserInRide(id, currentUser.getId()).isEmpty()) {
            return new ResponseEntity<GenericResponse>(
                    new GenericResponse("You are not in the ride already", HttpStatus.FORBIDDEN), HttpStatus.FORBIDDEN
            );
        }
        if (!currentUser.getEmail().equals(currentRide.getHost().getEmail())) {
            int temp = rideRepository.removeUserFromRide(id, currentUser.getId());
            if (temp != 1) {
                return new ResponseEntity<GenericResponse>(
                        new GenericResponse("Unable to remove you from Ride", HttpStatus.INTERNAL_SERVER_ERROR), HttpStatus.INTERNAL_SERVER_ERROR
                );
            }
            return new ResponseEntity<GenericResponse>(
                    new GenericResponse(currentRide, HttpStatus.OK), HttpStatus.OK
            );
        }
        else {
            rideRepository.deleteById(id);
            return new ResponseEntity<GenericResponse>(
                    new GenericResponse(currentRide, HttpStatus.OK), HttpStatus.OK
            );
        }

    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleValidationExceptions(
            MethodArgumentNotValidException ex
    ) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return errors;
    }

}
