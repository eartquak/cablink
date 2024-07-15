package com.oops.cablink.dtos;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Data;
import org.hibernate.validator.constraints.URL;

import java.math.BigInteger;

@Data
public class UserEditDTO {
        @Valid

        @NotBlank
        @NotEmpty
        private String name;

        @Digits(integer = 10, fraction = 0)
        @Min(value = 999999999)
        private BigInteger phNo;

        @URL
        private String dpURL;
}
