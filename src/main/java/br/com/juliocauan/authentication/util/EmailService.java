package br.com.juliocauan.authentication.util;

import org.openapitools.model.EmailType;
import org.springframework.stereotype.Service;

import br.com.juliocauan.authentication.infrastructure.exception.EmailException;
import br.com.juliocauan.authentication.util.emailers.Emailer;
import br.com.juliocauan.authentication.util.emailers.GmailEmailer;
import br.com.juliocauan.authentication.util.emailers.GreenMailEmailer;

@Service
public final class EmailService {

    private Emailer currentEmailer = null;

    public void sendEmail(String receiver, String subject, String message) {
        if (currentEmailer == null)
            throw new EmailException("Emailer not set. ADMIN must set one.");

        currentEmailer.sendSimpleEmail(receiver, subject, message);
    }

    public void setEmailer(String username, String key, EmailType emailerType) {
        if (emailerType == EmailType.GMAIL)
            currentEmailer = new GmailEmailer(username, key);
        
        if (emailerType == EmailType.GREEN_MAIL)
            currentEmailer = new GreenMailEmailer(username, key);
    }

}
