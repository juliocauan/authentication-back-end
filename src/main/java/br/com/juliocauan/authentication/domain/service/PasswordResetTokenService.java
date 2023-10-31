package br.com.juliocauan.authentication.domain.service;

import org.openapitools.model.PasswordMatch;

public abstract class PasswordResetTokenService {
    public abstract String generateToken(String username);
    public abstract String sendEmail(String username, String token);
    public abstract void resetPassword(PasswordMatch passwordMatch, String token);
}
