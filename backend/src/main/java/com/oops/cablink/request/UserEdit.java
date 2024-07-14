package com.oops.cablink.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigInteger;

@Data
public class UserEdit {
        @Valid

        @NotBlank
        @NotEmpty
        @NotNull
        private String name;

        @Digits(integer = 10, fraction = 0)
        @Min(value = 999999999)
        @NotNull
        private BigInteger phNo;
}
