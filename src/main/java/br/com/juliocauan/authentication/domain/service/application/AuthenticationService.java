package br.com.juliocauan.authentication.domain.service.application;

import org.openapitools.model.JWT;

public abstract class AuthenticationService {
    public abstract JWT authenticate(String username, String password);
    public abstract void registerUser(String username, String password);
    public abstract void registerAdmin(String username, String password, String adminKey);
}
