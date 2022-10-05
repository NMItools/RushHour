package com.internship.rushhour.infrastructure.validators;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class PasswordEncryptedValidator implements ConstraintValidator<EncryptedPassword, String> {
    @Override
    public void initialize(EncryptedPassword constraintAnnotation) {

    }

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        boolean isBcryptEncoded = true;
        try {
            encoder.upgradeEncoding(s);
        } catch (IllegalArgumentException e ){
            isBcryptEncoded = false;
        }
        return isBcryptEncoded;
    }
}
