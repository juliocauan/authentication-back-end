package br.com.juliocauan.authentication.util;

import org.openapitools.model.EmailType;

import br.com.juliocauan.authentication.infrastructure.exception.EmailException;
import br.com.juliocauan.authentication.util.emailers.Emailer;
import br.com.juliocauan.authentication.util.emailers.GmailEmailer;
import br.com.juliocauan.authentication.util.emailers.GreenMailEmailer;
import lombok.experimental.UtilityClass;

@UtilityClass
public final class EmailUtil {

    private static Emailer currentEmailer = null;

    public static void sendEmail(String receiver, String subject, String message) {
        if (currentEmailer == null)
            throw new EmailException("Emailer not set. ADMIN must set one.");

        currentEmailer.sendSimpleEmail(receiver, subject, message);
    }

    public static void setEmailer(String username, String key, EmailType emailerType) {
        switch (emailerType.getValue()) {
            case "GMAIL":
                currentEmailer = new GmailEmailer(username, key);
                break;
            case "GREEN_MAIL":
            default:
                currentEmailer = new GreenMailEmailer(username, key);
                break;
        }
    }

}
