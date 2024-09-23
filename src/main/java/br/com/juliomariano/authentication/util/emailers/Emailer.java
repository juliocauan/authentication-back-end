package br.com.juliomariano.authentication.util.emailers;

public interface Emailer {
    void sendSimpleEmail(String receiver, String subject, String message);
}
