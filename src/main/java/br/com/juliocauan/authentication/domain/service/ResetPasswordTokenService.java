package br.com.juliocauan.authentication.domain.service;

public interface ResetPasswordTokenService {
    void generateResetTokenAndSendEmail(String username);
}
