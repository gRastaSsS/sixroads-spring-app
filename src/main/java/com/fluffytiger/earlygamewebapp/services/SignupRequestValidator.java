package com.fluffytiger.earlygamewebapp.services;

import com.fluffytiger.earlygamewebapp.payload.SignupRequest;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Service
public class SignupRequestValidator implements Validator {
    @Override
    public boolean supports(Class<?> aClass) {
        return SignupRequest.class.equals(aClass);
    }

    @Override
    public void validate(Object o, Errors errors) {
        SignupRequest req = (SignupRequest)o;

        String username = req.getUsername();

        if (username.length() < 5)
            errors.rejectValue("username", "username.too-short");

        if (username.length() > 32)
            errors.rejectValue("username", "username.too-long");

        if (!username.matches("^[a-zA-Z0-9_]*$"))
            errors.rejectValue("username", "username.format");

        String password = req.getPassword();

        if (password.length() < 8)
            errors.rejectValue("password", "password.too-short");

        if (password.length() > 40)
            errors.rejectValue("password", "password.too-long");

        if (password.contains(" "))
            errors.rejectValue("password", "password.whitespace");

        if (!password.matches(".*\\d.*"))
            errors.rejectValue("password", "password.digit");

        if (!password.matches("^.*[a-zA-Z]+.*"))
            errors.rejectValue("password", "password.letter");
    }
}
