package br.com.juliocauan.authentication.domain.service;

public interface EmailService {
    void sendSimpleEmail(String receiver, String subject, String message);
}
