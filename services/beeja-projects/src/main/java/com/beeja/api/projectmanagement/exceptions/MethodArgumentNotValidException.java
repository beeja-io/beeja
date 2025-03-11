package com.beeja.api.projectmanagement.exceptions;

import org.springframework.validation.BindingResult;


public class MethodArgumentNotValidException extends RuntimeException{

    private final BindingResult bindingResult;

    public MethodArgumentNotValidException(BindingResult bindingResult) {
        super("Validation failed");
        this.bindingResult = bindingResult;
    }

    public BindingResult getBindingResult() {
        return bindingResult;
    }


}
