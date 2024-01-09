package br.com.juliocauan.authentication.util;

import org.openapitools.model.EmailType;

import br.com.juliocauan.authentication.infrastructure.exception.EmailException;
import br.com.juliocauan.authentication.util.emailers.Emailer;
import br.com.juliocauan.authentication.util.emailers.GmailEmailer;
import br.com.juliocauan.authentication.util.emailers.GreenMailEmailer;

public abstract class EmailUtil {

    private static Emailer currentEmailer = null;

    public static final void sendEmail(String receiver, String subject, String message) {
        if (currentEmailer == null)
            throw new EmailException("Emailer not set. ADMIN must set one.");

        currentEmailer.sendSimpleEmail(receiver, subject, message);
    }

    public static final void setEmailer(String username, String key, EmailType emailerType) {
        if (emailerType == EmailType.GMAIL)
            currentEmailer = new GmailEmailer(username, key);
        
        if (emailerType == EmailType.GREEN_MAIL)
            currentEmailer = new GreenMailEmailer(username, key);
    }

}
