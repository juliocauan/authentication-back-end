package br.com.juliocauan.authentication.domain.service.application;

import org.openapitools.model.EnumRole;
import org.openapitools.model.JWTResponse;
import org.openapitools.model.SigninForm;

public abstract class JwtService {
    public abstract JWTResponse authenticate(SigninForm signinForm);
    public abstract void validateAndRegisterNewUser(String username, String password, EnumRole role);
}
