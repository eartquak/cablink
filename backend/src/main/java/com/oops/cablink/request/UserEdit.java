package com.oops.cablink.request;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;

import java.math.BigInteger;

public record UserEdit (
        @NotBlank
        String name,

        @Digits(integer = 10, fraction = 0)
        BigInteger phNo
) {

}
