package br.com.juliomariano.authentication.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;

import org.junit.jupiter.api.Test;
import org.openapitools.model.EmailType;
import org.springframework.mail.MailSendException;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.juliomariano.authentication.config.TestContext;
import br.com.juliomariano.authentication.infrastructure.repository.RoleRepository;
import br.com.juliomariano.authentication.infrastructure.repository.UserRepository;
import br.com.juliomariano.authentication.util.emailers.GmailEmailer;
import br.com.juliomariano.authentication.util.emailers.GreenMailEmailer;

class EmailUtilTest extends TestContext {

    public EmailUtilTest(UserRepository userRepository, RoleRepository roleRepository,
            ObjectMapper objectMapper, MockMvc mockMvc) {
        super(userRepository, roleRepository, objectMapper, mockMvc);
    }

    @Test
    void setEmailer_null() {
        assertDoesNotThrow(() -> EmailUtil.setEmailer(getRandomUsername(), getRandomPassword(), null));
        assertEquals(null, EmailUtil.currentEmailer);
    }

    @Test
    void setEmailer_gmail() {
        assertDoesNotThrow(() -> EmailUtil.setEmailer(getRandomUsername(), getRandomPassword(), EmailType.GMAIL));
        assertTrue(EmailUtil.currentEmailer instanceof GmailEmailer);
    }

    @Test
    void setEmailer_greenMail() {
        assertDoesNotThrow(() -> EmailUtil.setEmailer(getRandomUsername(), getRandomPassword(), EmailType.GREEN_MAIL));
        assertTrue(EmailUtil.currentEmailer instanceof GreenMailEmailer);
    }

    @Test
    void sendEmail() {
        assertDoesNotThrow(() -> EmailUtil.sendEmail(getRandomUsername(), "Test Subject", "Test Message"));
    }

    @Test
    void sendEmail_error_null() {
        EmailUtil.setEmailer(getRandomUsername(), getRandomPassword(), null);
        MailSendException exception = assertThrowsExactly(MailSendException.class,
            () -> EmailUtil.sendEmail(getRandomUsername(), "Test Subject", "Test Message"));
        assertTrue(exception.getMessage().equals("Emailer not set. ADMIN must set one."));
    }

}
