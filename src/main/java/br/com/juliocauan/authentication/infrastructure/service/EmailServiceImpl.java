package br.com.juliocauan.authentication.infrastructure.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import br.com.juliocauan.authentication.domain.service.EmailService;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;
    
    //TODO review this
    private static final String SENDER_EMAIL = "jcam.test01@gmail.com";
    
    @Override
    public void sendEmail(String receiver, String subject, String message) {
        SimpleMailMessage email = new SimpleMailMessage();
        email.setFrom(SENDER_EMAIL);
        email.setTo(receiver);
        email.setSubject(subject);
        email.setText(message);
        mailSender.send(email);
    }
    
}
