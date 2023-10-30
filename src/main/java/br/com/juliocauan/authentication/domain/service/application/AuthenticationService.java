package br.com.juliocauan.authentication.domain.service.application;

import org.openapitools.model.EnumRole;
import org.openapitools.model.JWTResponse;

public abstract class AuthenticationService {
    public abstract JWTResponse authenticate(String username, String password);
    public abstract void validateAndRegisterNewUser(String username, String password, EnumRole role);
}
