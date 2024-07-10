package com.example.cablink.request_model;


import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.math.BigInteger;

public record UserCreate (
        @Digits(integer = 10, fraction = 0)
        BigInteger phNo
        ) {
}
