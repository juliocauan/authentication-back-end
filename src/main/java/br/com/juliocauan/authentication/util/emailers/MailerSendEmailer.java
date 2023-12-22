package br.com.juliocauan.authentication.util.emailers;

import com.mailersend.sdk.MailerSend;
import com.mailersend.sdk.emails.Email;
import com.mailersend.sdk.exceptions.MailerSendException;

import br.com.juliocauan.authentication.infrastructure.exception.EmailException;

public final class MailerSendEmailer implements Emailer {

    private final MailerSend mailerSend = new MailerSend();
    private String from = "";

    @Override
    public void sendEmail(String receiver, String subject, String message) {
        Email email = new Email();

        email.setFrom(from, from);
        email.addRecipient(receiver, receiver);
        email.setSubject(subject);
        email.setPlain(message);

        try {
            mailerSend.emails().send(email);
        } catch (MailerSendException ex) {
            throw new EmailException(ex.getMessage());
        }
    }

    @Override
    public void configure(String username, String token) {
        this.from = username;
        mailerSend.setToken(token);
    }

}
