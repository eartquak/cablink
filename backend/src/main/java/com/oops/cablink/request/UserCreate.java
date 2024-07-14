package com.oops.cablink.request;


import jakarta.validation.Valid;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.math.BigInteger;

@Data
public class UserCreate {
        @Valid

        @NotNull(message = "cannot be null")
        @Digits(integer = 10, fraction = 0)
        @Min(value = 999999999)
        private BigInteger phNo;
}
