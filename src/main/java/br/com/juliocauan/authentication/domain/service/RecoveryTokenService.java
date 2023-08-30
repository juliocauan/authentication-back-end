package br.com.juliocauan.authentication.domain.service;

public interface RecoveryTokenService {
    void generateLinkAndSendEmail(String username);
}
