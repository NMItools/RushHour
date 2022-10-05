package com.internship.rushhour.infrastructure.exceptions;

public class AccessingLockedFieldException extends  RuntimeException{
    public AccessingLockedFieldException(String role, String field){
        super("A " + role + " is attempting to change " + field + " which is locked for their role");
    }

}
