package com.oops.cablink.dtos.requests;


import com.oops.cablink.models.User;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigInteger;

@Data
public class UserCreateRequestDTO {
        @Valid

        @NotNull(message = "cannot be null")
        @Digits(integer = 10, fraction = 0)
        @Min(value = 999999999)
        private BigInteger phNo;

        @NotNull
        User.UserType userType;
}
