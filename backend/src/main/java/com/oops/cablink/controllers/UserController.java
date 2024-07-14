package com.oops.cablink.controllers;

import com.oops.cablink.services.UserService;
import com.oops.cablink.repositories.UserRepository;
import com.oops.cablink.models.User;
import com.oops.cablink.repositories.RideRepository;
import com.oops.cablink.dtos.UserCreateDTO;
import com.oops.cablink.dtos.UserEditDTO;
import com.oops.cablink.response.GenericResponse;

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

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
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

            @Valid
            @RequestBody
            UserCreateDTO userCreateDTO
            ) {
        GenericResponse userResponse = userService.getUser(principal);
        if (userResponse.httpStatus == HttpStatus.OK) {
            return new ResponseEntity<GenericResponse>(userResponse, HttpStatus.CONFLICT);
        }

        User newUser = new User(
                new ObjectId(),
                Objects.requireNonNull(principal.getAttribute("name")).toString(),
                Objects.requireNonNull(principal.getAttribute("email")).toString(),
                userCreateDTO.getPhNo()
        );

        return new ResponseEntity<GenericResponse>(
                new GenericResponse( userRepository.save(newUser).block(),  HttpStatus.CREATED),
                HttpStatus.CREATED
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
                new GenericResponse( currentUser, HttpStatus.OK),
                HttpStatus.OK
        );
    }

    @PostMapping("/user/edit")
    public ResponseEntity<GenericResponse> edit(
            @AuthenticationPrincipal
            OAuth2User principal,

            @RequestBody
            @Valid
            UserEditDTO userEditDTO) {

        GenericResponse userResponse = userService.getUser(principal);
        if (userResponse.httpStatus != HttpStatus.OK) {
            return new ResponseEntity<GenericResponse>(userResponse, userResponse.httpStatus);
        }

        final User currentUser = (User)userResponse.data;

        if (userEditDTO == null) {
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

        if (userEditDTO.getName() != null) {
            newName = userEditDTO.getName();
        }
        if (userEditDTO.getPhNo() != null) {
            newPhNo = userEditDTO.getPhNo();
        }

        User user = new User(
                currentUser.getId(),
                newName,
                currentUser.getEmail(),
                newPhNo
        );

        return new ResponseEntity<GenericResponse>(
                new GenericResponse(userRepository.save(user), HttpStatus.OK),
                HttpStatus.OK
        );
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


        ObjectId id = currentUser.getId();

        //TODO: Implement This
        return new ResponseEntity<GenericResponse>(new GenericResponse(
                rideRepository.findRidesByHost(id), HttpStatus.OK),
                HttpStatus.OK
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

        ObjectId id = currentUser.getId();
        userRepository.deleteById(id);

        return new ResponseEntity<GenericResponse>(
                new GenericResponse( currentUser, HttpStatus.OK),
                HttpStatus.OK
        );
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
