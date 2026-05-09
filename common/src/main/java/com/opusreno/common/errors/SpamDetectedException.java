package com.opusreno.common.errors;

public class SpamDetectedException extends RuntimeException {

    public SpamDetectedException(String reason) {
        super(reason);
    }
}
