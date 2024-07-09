package com.example.cablink.controllers;

import com.example.cablink.models.User;
import com.example.cablink.repositories.UserRepository;

import com.example.cablink.response.GenericResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@RestController
public class UserController {

    @Autowired
    UserRepository userRepository;

    @GetMapping("/user/me")
    public ResponseEntity<GenericResponse> getMe(
            @AuthenticationPrincipal
            OAuth2User principal
    ) {if (principal.getAttribute("email") == null || Objects.requireNonNull(principal.getAttribute("email")).toString().isEmpty() || Objects.requireNonNull(
            principal.getAttribute("email")).toString().isBlank()) {
        return new ResponseEntity<GenericResponse>(
                new GenericResponse("Error", GenericResponse.ResponseStatus.ERROR),
                HttpStatus.UNAUTHORIZED
        );
    }
        final User currentUser = userRepository.findByEmail(Objects.requireNonNull(principal.getAttribute("email")).toString());

        if (currentUser == null) {
            return new ResponseEntity<GenericResponse>(
                    new GenericResponse("Error", GenericResponse.ResponseStatus.ERROR),
                    HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<GenericResponse>(
                new GenericResponse( currentUser, GenericResponse.ResponseStatus.SUCCESS),
                HttpStatus.OK);
    }



}
