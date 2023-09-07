package br.com.juliocauan.authentication.domain.service;

public interface EmailService {
    void sendEmail(String receiver, String subject, String message);
}
