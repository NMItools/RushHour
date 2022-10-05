package com.internship.rushhour.infrastructure.exceptions;

public class UserActionNeededException extends RuntimeException{
    /** This exception is used when an issue that is occurring can be resolved by user action e.g. updating their
     * account information
     * */
    public UserActionNeededException(String message){
        super(message);
    }
}
