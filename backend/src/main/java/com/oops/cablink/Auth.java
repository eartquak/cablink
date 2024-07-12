package com.oops.cablink;

import com.oops.cablink.models.User;
import com.oops.cablink.repositories.UserRepository;
import com.oops.cablink.response.GenericResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class Auth {

    @Autowired
    UserRepository userRepository;

    public GenericResponse getUser(
            @AuthenticationPrincipal
            OAuth2User principal
    ) {

        if (principal.getAttribute("email") == null || Objects.requireNonNull(principal.getAttribute("email")).toString().isEmpty() || Objects.requireNonNull(
                principal.getAttribute("email")).toString().isBlank()) {
            return new GenericResponse("Error", GenericResponse.ResponseStatus.ERROR);
        }

        final User currentUser = userRepository.findByEmail(Objects.requireNonNull(principal.getAttribute("email")).toString()).blockFirst();
        if (currentUser == null) {
            return new GenericResponse("Could not find user", GenericResponse.ResponseStatus.ERROR);
        }

        return new GenericResponse(currentUser, GenericResponse.ResponseStatus.SUCCESS);
    }
}
