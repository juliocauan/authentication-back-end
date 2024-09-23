package br.com.juliomariano.authentication.util.emailers;

import java.util.Properties;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

public final class GmailEmailer implements Emailer {

    private final JavaMailSender mailSender;
    private final String sender;

    public GmailEmailer(String username, String accessKey) {
        JavaMailSenderImpl javaMailSenderImpl = new JavaMailSenderImpl();
        javaMailSenderImpl.setHost("smtp.gmail.com");
        javaMailSenderImpl.setPort(587);

        Properties props = javaMailSenderImpl.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");

        javaMailSenderImpl.setUsername(username);
        javaMailSenderImpl.setPassword(accessKey);

        this.mailSender = javaMailSenderImpl;
        this.sender = username;
    }

    @Override
    public void sendSimpleEmail(String receiver, String subject, String message) {
        SimpleMailMessage email = new SimpleMailMessage();

        email.setFrom(this.sender);
        email.setTo(receiver);
        email.setSubject(subject);
        email.setText(message);
        
        mailSender.send(email);
    }

}
