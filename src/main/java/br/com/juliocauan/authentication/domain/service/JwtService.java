package br.com.juliocauan.authentication.domain.service;

import org.openapitools.model.JWTResponse;
import org.openapitools.model.SigninForm;
import org.openapitools.model.SignupForm;

public interface JwtService {
    JWTResponse authenticate(SigninForm signinForm);
    void validateAndRegisterNewUser(SignupForm signupForm);
}
