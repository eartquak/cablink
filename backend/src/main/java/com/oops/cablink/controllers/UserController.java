package com.oops.cablink.controllers;

import com.oops.cablink.services.UserService;
import com.oops.cablink.repositories.UserRepository;
import com.oops.cablink.models.User;
import com.oops.cablink.repositories.RideRepository;

import com.oops.cablink.request.UserCreate;
import com.oops.cablink.request.UserEdit;
import com.oops.cablink.response.GenericResponse;
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

    @Autowired
    UserService userService;


    @PostMapping("/user/create")
    public ResponseEntity<GenericResponse> create(
            @AuthenticationPrincipal
            OAuth2User principal,
            @RequestBody
            @Validated
            UserCreate userCreate) {
        if (userCreate == null) {
            return new ResponseEntity<GenericResponse>(
                    new GenericResponse( "", HttpStatus.UNAUTHORIZED), HttpStatus.UNAUTHORIZED
            );
        }
        if (principal.getAttribute("email") == null || Objects.requireNonNull(principal.getAttribute("email")).toString().isEmpty() || Objects.requireNonNull(
                principal.getAttribute("email")).toString().isBlank()) {
            return new ResponseEntity<GenericResponse>(
                    new GenericResponse("Error", HttpStatus.UNAUTHORIZED),HttpStatus.UNAUTHORIZED
            );
        }

        if (principal.getAttribute("name") == null || Objects.requireNonNull(principal.getAttribute("name")).toString().isEmpty() || Objects.requireNonNull(
                principal.getAttribute("name")).toString().isBlank()) {
            return new ResponseEntity<GenericResponse>(
                    new GenericResponse("Error", HttpStatus.UNAUTHORIZED),HttpStatus.UNAUTHORIZED
            );
        }

        if (userRepository.findByEmail(Objects.requireNonNull(principal.getAttribute("email")).toString()).blockFirst() != null) {
            return new ResponseEntity<GenericResponse>(
                    new GenericResponse( "Error", HttpStatus.UNAUTHORIZED),HttpStatus.BAD_REQUEST
            );
        }

        User user = new User(
                new ObjectId(),
                Objects.requireNonNull(principal.getAttribute("name")).toString(),
                Objects.requireNonNull(principal.getAttribute("email")).toString(),
                userCreate.phNo()
        );

        return new ResponseEntity<GenericResponse>(
                new GenericResponse( userRepository.save(user).block(),  HttpStatus.CREATED), HttpStatus.CREATED
        );
    }

    @GetMapping("/user/me")
    public ResponseEntity<GenericResponse> getMe (
            @AuthenticationPrincipal
            OAuth2User principal
    ) {
        GenericResponse userResponse = userService.getUser(principal);
        if (userResponse.httpStatus != HttpStatus.OK) {
            return new ResponseEntity<GenericResponse>(userResponse, userResponse.httpStatus);
        }

        final User currentUser = (User)userResponse.data;

        return new ResponseEntity<GenericResponse>(
                new GenericResponse( currentUser, HttpStatus.OK), HttpStatus.OK
        );
    }

    @PostMapping("/user/edit")
    public ResponseEntity<GenericResponse> edit(
            @AuthenticationPrincipal
            OAuth2User principal,
            @RequestBody
            @Validated
            UserEdit userEdit) {

        GenericResponse userResponse = userService.getUser(principal);
        if (userResponse.httpStatus != HttpStatus.OK) {
            return new ResponseEntity<GenericResponse>(userResponse, userResponse.httpStatus);
        }

        final User currentUser = (User)userResponse.data;

        if (userEdit == null) {
            return new ResponseEntity<GenericResponse>(
                    new GenericResponse(
                            "Error",
                            HttpStatus.UNAUTHORIZED
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
                newName,
                currentUser.getEmail(),
                newPhNo
        );

        return new ResponseEntity<GenericResponse>(new GenericResponse(
                userRepository.save(user),
                HttpStatus.OK
        ), HttpStatus.OK);
    }

    @GetMapping("/user/rides")
    public ResponseEntity<GenericResponse> getUserRides(
            @AuthenticationPrincipal
            OAuth2User principal
    ) {

        GenericResponse userResponse = userService.getUser(principal);
        if (userResponse.httpStatus != HttpStatus.OK) {
            return new ResponseEntity<GenericResponse>(userResponse, userResponse.httpStatus);
        }

        final User currentUser = (User)userResponse.data;

        if (currentUser == null) {
            return new ResponseEntity<GenericResponse>(new GenericResponse(
                    "Error",
                    HttpStatus.UNAUTHORIZED
            ), HttpStatus.UNAUTHORIZED);
        }

        ObjectId id = currentUser.getId();
        System.out.println(id);

        //TODO: Implement This
        return new ResponseEntity<GenericResponse>(new GenericResponse(
                rideRepository.findRidesByHost(id), HttpStatus.OK), HttpStatus.OK
        );
    }


    @GetMapping("/user/delete")
    public ResponseEntity<GenericResponse> deleteMe (
            @AuthenticationPrincipal
            OAuth2User principal
    ) {

        GenericResponse userResponse = userService.getUser(principal);
        if (userResponse.httpStatus != HttpStatus.OK) {
            return new ResponseEntity<GenericResponse>(userResponse, userResponse.httpStatus);
        }

        final User currentUser = (User)userResponse.data;

        if (currentUser == null) {
            return new ResponseEntity<GenericResponse>(
                    new GenericResponse("Could not find user", HttpStatus.UNAUTHORIZED), HttpStatus.NOT_FOUND
            );
        }


        ObjectId id = currentUser.getId();
        userRepository.deleteById(id);

        return new ResponseEntity<GenericResponse>(
                new GenericResponse( currentUser, HttpStatus.OK), HttpStatus.OK
        );
    }
}
