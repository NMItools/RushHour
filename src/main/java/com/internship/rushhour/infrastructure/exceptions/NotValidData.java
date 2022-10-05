package com.internship.rushhour.infrastructure.exceptions;

public class NotValidData extends RuntimeException{

    public NotValidData(String message){
        super(message);
    }
}
