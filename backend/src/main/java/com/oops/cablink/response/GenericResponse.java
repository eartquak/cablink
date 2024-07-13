package com.oops.cablink.response;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
public class GenericResponse {

    public Object data;
    public HttpStatus httpStatus;

}
