package br.com.juliocauan.authentication.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.mail.MailSendException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
public final class EmailUtil {

    private static JavaMailSender mailSender;
    private static Environment env;

    @Autowired
    private void setMailSender(JavaMailSender mailSender) {
        EmailUtil.mailSender = mailSender;
    }

    @Autowired
    private void setEnvironment(Environment env) {
        EmailUtil.env = env;
    }
    
    public static final void sendEmail(String receiver, String subject, String message) {
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
