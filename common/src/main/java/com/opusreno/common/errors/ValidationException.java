package com.opusreno.common.errors;

import java.util.List;

public class ValidationException extends RuntimeException {

    private final List<String> details;

    public ValidationException(List<String> details) {
        super("validation failed");
        this.details = details;
    }

    public List<String> getDetails() { return details; }
}
