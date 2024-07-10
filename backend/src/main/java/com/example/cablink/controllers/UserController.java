package com.example.cablink.controllers;

import com.example.cablink.models.Ride;
import com.example.cablink.models.User;
import com.example.cablink.repositories.RideRepository;
import com.example.cablink.repositories.UserRepository;

import com.example.cablink.request_model.UserCreate;
import com.example.cablink.request_model.UserEdit;
import com.example.cablink.response.GenericResponse;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigInteger;
import java.util.Objects;

@RestController
public class UserController {

    @Autowired
    UserRepository userRepository;

    @Autowired
    RideRepository rideRepository;


    @PostMapping("/user/create")
    public ResponseEntity<GenericResponse> create(
            @AuthenticationPrincipal
            OAuth2User principal,
            @RequestBody
            @Validated
            UserCreate userCreate) {
        if (userCreate == null) {
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

        if (principal.getAttribute("name") == null || Objects.requireNonNull(principal.getAttribute("name")).toString().isEmpty() || Objects.requireNonNull(
                principal.getAttribute("name")).toString().isBlank()) {
            return new ResponseEntity<GenericResponse>(
                    new GenericResponse("Error", GenericResponse.ResponseStatus.ERROR),HttpStatus.UNAUTHORIZED
            );
        }

        if (userRepository.findByEmail(Objects.requireNonNull(principal.getAttribute("email")).toString()) != null) {
            return new ResponseEntity<GenericResponse>(
                    new GenericResponse( "Error", GenericResponse.ResponseStatus.ERROR),HttpStatus.BAD_REQUEST
            );
        }

        User user = new User(
                new ObjectId(),
                Objects.requireNonNull(principal.getAttribute("name")).toString(),
                Objects.requireNonNull(principal.getAttribute("email")).toString(),
                userCreate.phNo()
        );
        return new ResponseEntity<GenericResponse>(
                new GenericResponse( userRepository.save(user),  GenericResponse.ResponseStatus.SUCCESS), HttpStatus.CREATED
        );
    }

    @GetMapping("/user/me")
    public ResponseEntity<GenericResponse> getMe (
            @AuthenticationPrincipal
            OAuth2User principal
    ) {
        if (principal.getAttribute("email") == null || Objects.requireNonNull(principal.getAttribute("email")).toString().isEmpty() || Objects.requireNonNull(
            principal.getAttribute("email")).toString().isBlank()) {
        return new ResponseEntity<GenericResponse>(
                new GenericResponse("Error", GenericResponse.ResponseStatus.ERROR), HttpStatus.UNAUTHORIZED
        );
    }
        final User currentUser = userRepository.findByEmail(Objects.requireNonNull(principal.getAttribute("email")).toString());

        if (currentUser == null) {
            return new ResponseEntity<GenericResponse>(
                    new GenericResponse("Could not find user", GenericResponse.ResponseStatus.ERROR), HttpStatus.NOT_FOUND
            );
        }
        return new ResponseEntity<GenericResponse>(
                new GenericResponse( currentUser, GenericResponse.ResponseStatus.SUCCESS), HttpStatus.OK
        );
    }

    @PostMapping("/user/edit")
    public ResponseEntity<GenericResponse> edit(
            @AuthenticationPrincipal
            OAuth2User principal,
            @RequestBody
            @Validated
            UserEdit userEdit) {
        if (principal.getAttribute("email") == null || Objects.requireNonNull(principal.getAttribute("email")).toString().isEmpty() || Objects.requireNonNull(
                principal.getAttribute("email")).toString().isBlank()) {
            return new ResponseEntity<GenericResponse>(
                    new GenericResponse(
                            "Error",
                            GenericResponse.ResponseStatus.ERROR
                    ),
                    HttpStatus.UNAUTHORIZED
            );
        }
        final User currentUser = userRepository.findByEmail(Objects.requireNonNull(principal.getAttribute("email")).toString());
        if (currentUser == null) {
            return new ResponseEntity<GenericResponse>(new GenericResponse(
                    "Error",
                    GenericResponse.ResponseStatus.ERROR
            ), HttpStatus.UNAUTHORIZED);
        }
        if (userEdit == null) {
            return new ResponseEntity<GenericResponse>(
                    new GenericResponse(
                            "Error",
                            GenericResponse.ResponseStatus.ERROR
                    ),
                    HttpStatus.BAD_REQUEST
            );
        }

        String newName = currentUser.getName();
        BigInteger newPhNo = currentUser.getPhNo();

        if (userEdit.name() != null) {
            newName = userEdit.name();
        }
        if (userEdit.phNo() != null) {
            newPhNo = userEdit.phNo();
        }

        User user = new User(
                currentUser.getId(),
                currentUser.getName(),
                currentUser.getEmail(),
                newPhNo
        );

        return new ResponseEntity<GenericResponse>(new GenericResponse(
                userRepository.save(user),
                GenericResponse.ResponseStatus.SUCCESS
        ), HttpStatus.OK);
    }

    @GetMapping("/user/rides")
    public ResponseEntity<GenericResponse> getUserRides(
            @AuthenticationPrincipal
            OAuth2User principal
    ) {
        if (principal.getAttribute("email") == null || Objects.requireNonNull(principal.getAttribute("email")).toString().isEmpty() || Objects.requireNonNull(
                principal.getAttribute("email")).toString().isBlank()) {
            return new ResponseEntity<GenericResponse>(
                    new GenericResponse(
                            "Error",
                            GenericResponse.ResponseStatus.ERROR
                    ),
                    HttpStatus.UNAUTHORIZED
            );
        }

        final User currentUser = userRepository.findByEmail(Objects.requireNonNull(principal.getAttribute("email")).toString());
        if (currentUser == null) {
            return new ResponseEntity<GenericResponse>(new GenericResponse(
                    "Error",
                    GenericResponse.ResponseStatus.ERROR
            ), HttpStatus.UNAUTHORIZED);
        }

        ObjectId id = currentUser.getId();
        System.out.println(id);

        //TODO: Implement This
        return new ResponseEntity<GenericResponse>(new GenericResponse(
                rideRepository.findRidesByHost(id), GenericResponse.ResponseStatus.SUCCESS), HttpStatus.OK
        );
    }


    @GetMapping("/user/delete")
    public ResponseEntity<GenericResponse> deleteMe (
            @AuthenticationPrincipal
            OAuth2User principal
    ) {
        if (principal.getAttribute("email") == null || Objects.requireNonNull(principal.getAttribute("email")).toString().isEmpty() || Objects.requireNonNull(
                principal.getAttribute("email")).toString().isBlank()) {
            return new ResponseEntity<GenericResponse>(
                    new GenericResponse("Error", GenericResponse.ResponseStatus.ERROR), HttpStatus.UNAUTHORIZED
            );
        }

        final User currentUser = userRepository.findByEmail(Objects.requireNonNull(principal.getAttribute("email")).toString());
        if (currentUser == null) {
            return new ResponseEntity<GenericResponse>(
                    new GenericResponse("Could not find user", GenericResponse.ResponseStatus.ERROR), HttpStatus.NOT_FOUND
            );
        }


        ObjectId id = currentUser.getId();
        userRepository.deleteById(id);

        return new ResponseEntity<GenericResponse>(
                new GenericResponse( currentUser, GenericResponse.ResponseStatus.SUCCESS), HttpStatus.OK
        );
    }

//    @GetMapping("/user/all")
//    public ResponseEntity<GenericResponse> getAll (
//            @AuthenticationPrincipal
//            OAuth2User principal
//    ) {
//        return new ResponseEntity<GenericResponse>(
//                new GenericResponse(userRepository.findAll(), GenericResponse.ResponseStatus.SUCCESS), HttpStatus.OK
//        );
//    }
}
