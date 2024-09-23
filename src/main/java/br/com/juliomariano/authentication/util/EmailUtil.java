package br.com.juliomariano.authentication.util;

import org.openapitools.model.EmailType;
import org.springframework.mail.MailSendException;

import br.com.juliomariano.authentication.util.emailers.Emailer;
import br.com.juliomariano.authentication.util.emailers.GmailEmailer;
import br.com.juliomariano.authentication.util.emailers.GreenMailEmailer;
import lombok.experimental.UtilityClass;

@UtilityClass
public final class EmailUtil {

    protected static Emailer currentEmailer = null;

    public static void sendEmail(String receiver, String subject, String message) {
        if (currentEmailer == null)
            throw new MailSendException("Emailer not set. ADMIN must set one.");

        currentEmailer.sendSimpleEmail(receiver, subject, message);
    }

    public static void setEmailer(String username, String key, EmailType emailerType) {
        if(emailerType == null) {
            currentEmailer = null;
            return;
        }
        
        switch (emailerType) {
            case GMAIL:
                currentEmailer = new GmailEmailer(username, key);
                break;
            case GREEN_MAIL:
                currentEmailer = new GreenMailEmailer(username, key);
                break;
        }
    }

}
