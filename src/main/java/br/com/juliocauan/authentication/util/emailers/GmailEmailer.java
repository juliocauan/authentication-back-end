package br.com.juliocauan.authentication.util.emailers;

import org.springframework.mail.MailSendException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Component;

import br.com.juliocauan.authentication.infrastructure.exception.EmailException;
import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public final class GmailEmailer implements Emailer {

    private final JavaMailSenderImpl mailSender;

    @Override
    public void sendEmail(String receiver, String subject, String message) {
        SimpleMailMessage email = new SimpleMailMessage();

        email.setFrom(mailSender.getUsername());
        email.setTo(receiver);
        email.setSubject(subject);
        email.setText(message);
        
        try{
            mailSender.send(email);
        } catch (MailSendException e) {
            throw new EmailException("The recipient address is not a valid address!");
        }
    }

    @Override
    public void configure(String username, String key) {
        mailSender.setUsername(username);
        mailSender.setPassword(key);
    }

}
