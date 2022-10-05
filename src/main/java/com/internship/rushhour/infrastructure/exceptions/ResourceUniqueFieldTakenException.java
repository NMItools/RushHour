package com.internship.rushhour.infrastructure.exceptions;

public class ResourceUniqueFieldTakenException extends  RuntimeException {
    public ResourceUniqueFieldTakenException(Long resourceIdentifier, String resourceName){
        super(String.format("%s with id %d already exists", resourceName, resourceIdentifier));
    }

    public ResourceUniqueFieldTakenException(String resourceIdentifier, String resourceName){
        super(String.format("%s with %s already exists", resourceName, resourceIdentifier));
    }

    public ResourceUniqueFieldTakenException(String entityName, String fieldName, String fieldValue){
        super(String.format("%s with %s = %s already exists", entityName, fieldName, fieldValue));
    }
}
