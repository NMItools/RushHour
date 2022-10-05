package com.internship.rushhour.infrastructure.exceptions;

public class InvalidStartTimeException extends RuntimeException {
    public InvalidStartTimeException() {
        super("The appointment date is in the past");
    }
}