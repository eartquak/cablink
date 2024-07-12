package com.oops.cablink.response;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class GenericResponse {

    public Object data;
    public ResponseStatus status;

    public enum ResponseStatus {
        SUCCESS,
        ERROR,
    }
}
