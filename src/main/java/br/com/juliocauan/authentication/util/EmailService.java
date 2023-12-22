package br.com.juliocauan.authentication.util;

import org.openapitools.model.EmailType;
import org.springframework.stereotype.Service;

import br.com.juliocauan.authentication.infrastructure.exception.EmailException;
import br.com.juliocauan.authentication.util.emailers.Emailer;
import br.com.juliocauan.authentication.util.emailers.GmailEmailer;
import br.com.juliocauan.authentication.util.emailers.MailerSendEmailer;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public final class EmailService {

    private final GmailEmailer gmailEmailer;
    private final MailerSendEmailer mailerSendEmailer = new MailerSendEmailer();

    private Emailer currentEmailer;

    public void sendEmail(String receiver, String subject, String message) {
        if (currentEmailer == null)
            throw new EmailException("Emailer not set. Call setEmailer first.");

        currentEmailer.sendEmail(receiver, subject, message);
    }

    public void setEmailer(String username, String key, EmailType emailerType) {
        if (emailerType == EmailType.GMAIL)
            setAsGmailSender(username, key);

        if (emailerType == EmailType.MAILER_SEND)
            setAsMailerSend(username, key);
    }

    private void setAsGmailSender(String username, String password) {
        gmailEmailer.configure(username, password);
        currentEmailer = gmailEmailer;
    }

    private void setAsMailerSend(String username, String token) {
        mailerSendEmailer.configure(username, token);
        currentEmailer = mailerSendEmailer;
    }

}
