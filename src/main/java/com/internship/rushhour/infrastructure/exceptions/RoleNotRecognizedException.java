package com.internship.rushhour.infrastructure.exceptions;

public class RoleNotRecognizedException extends RuntimeException{
    public RoleNotRecognizedException(){
        super("Role not recognized!");
    }
}
