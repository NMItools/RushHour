package com.internship.rushhour.infrastructure.exceptions;

public class OutsideProviderWorkingHoursException extends RuntimeException {
    public OutsideProviderWorkingHoursException(){
        super("The appointment is outside of the provider's working hours");
    }
}
