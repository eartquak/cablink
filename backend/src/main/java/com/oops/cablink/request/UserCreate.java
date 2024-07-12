package com.oops.cablink.request;


import jakarta.validation.constraints.Digits;

import java.math.BigInteger;

public record UserCreate (
        @Digits(integer = 10, fraction = 0)
        BigInteger phNo
        ) {
}
