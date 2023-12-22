package br.com.juliocauan.authentication.util;

import org.springframework.stereotype.Service;

import br.com.juliocauan.authentication.infrastructure.exception.EmailException;
import br.com.juliocauan.authentication.util.emailers.Emailer;
import br.com.juliocauan.authentication.util.emailers.GmailEmailer;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public final class EmailService {

    private final GmailEmailer gmailEmailer;

    private Emailer currentEmailer;
    
    public void setAsGmailSender(String username, String password) {
        gmailEmailer.configure(username, password);
        currentEmailer = gmailEmailer;
    }
    
    public void sendEmail(String receiver, String subject, String message) {
        if (currentEmailer == null)
            throw new EmailException("Emailer not set. Call setAsGmailSender first.");

        currentEmailer.sendEmail(receiver, subject, message);
    }
    
}
