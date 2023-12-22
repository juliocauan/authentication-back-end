package br.com.juliocauan.authentication.util.emailers;

public interface Emailer {
    void sendEmail(String receiver, String subject, String message);
    void configure(String username, String key);
}
