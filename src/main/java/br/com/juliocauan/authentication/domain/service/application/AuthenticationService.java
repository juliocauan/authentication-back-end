package br.com.juliocauan.authentication.domain.service.application;

import org.openapitools.model.JWT;
import org.openapitools.model.PasswordMatch;

public abstract class AuthenticationService {
    public abstract JWT authenticate(String username, String password);
    public abstract void registerUser(String username, PasswordMatch password);
}
