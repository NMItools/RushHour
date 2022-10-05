package com.internship.rushhour.infrastructure.exceptions;

public class ResourceNotFoundException extends RuntimeException{
    public ResourceNotFoundException(Long resourceIdentifier, String field, String resourceName){
        super(String.format("%s with %s = %d not found", resourceName, field, resourceIdentifier));
    }

    public ResourceNotFoundException(String resourceIdentifier, String resourceName, String field){
        super(String.format("%s with %s = %s not found", resourceName, field, resourceIdentifier));
    }
}
