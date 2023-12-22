package br.com.juliocauan.authentication.util;

import org.openapitools.model.EmailType;
import org.springframework.stereotype.Service;

import br.com.juliocauan.authentication.infrastructure.exception.EmailException;
import br.com.juliocauan.authentication.util.emailers.Emailer;
import br.com.juliocauan.authentication.util.emailers.GmailEmailer;
import br.com.juliocauan.authentication.util.emailers.GreenMailEmailer;
import br.com.juliocauan.authentication.util.emailers.MailerSendEmailer;

@Service
public final class EmailService {

    private final GmailEmailer gmailEmailer;
    private final MailerSendEmailer mailerSendEmailer;
    private final GreenMailEmailer greenMailEmailer;

    private Emailer currentEmailer;

    public EmailService(GmailEmailer gmailEmailer) {
        this.gmailEmailer = gmailEmailer;
        this.mailerSendEmailer = new MailerSendEmailer();
        this.greenMailEmailer = new GreenMailEmailer();
        this.currentEmailer = null;
    }

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
        
        if (emailerType == EmailType.GREEN_MAIL)
            setAsGreenMail(username, key);
    }

    private void setAsGmailSender(String username, String password) {
        gmailEmailer.configure(username, password);
        currentEmailer = gmailEmailer;
    }

    private void setAsMailerSend(String username, String token) {
        mailerSendEmailer.configure(username, token);
        currentEmailer = mailerSendEmailer;
    }

    private void setAsGreenMail(String username, String password) {
        greenMailEmailer.configure(username, password);
        currentEmailer = greenMailEmailer;
    }

}
