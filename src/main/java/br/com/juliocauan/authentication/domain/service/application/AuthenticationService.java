package br.com.juliocauan.authentication.domain.service.application;

import org.openapitools.model.BearerToken;
import org.openapitools.model.EnumRole;

public abstract class AuthenticationService {
    public abstract BearerToken authenticate(String username, String password);
    public abstract void validateAndRegisterNewUser(String username, String password, EnumRole role);
}
