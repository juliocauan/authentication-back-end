package br.com.juliocauan.authentication.util;

import org.springframework.core.env.Environment;
import org.springframework.mail.MailSendException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public final class EmailService {

    private final JavaMailSender mailSender;
    private final Environment env;
    
    public final void sendEmail(String receiver, String subject, String message) {
        SimpleMailMessage email = new SimpleMailMessage();
        email.setFrom(env.getProperty("spring.mail.username"));
        email.setTo(receiver);
        email.setSubject(subject);
        email.setText(message);
        try{
            mailSender.send(email);
        } catch (MailSendException e) {
            throw new MailSendException("The recipient address is not a valid address!");
        }
    }
    
}
