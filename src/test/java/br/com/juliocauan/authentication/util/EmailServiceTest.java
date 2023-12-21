package br.com.juliocauan.authentication.util;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;

import org.junit.jupiter.api.Test;
import org.springframework.mail.MailSendException;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.juliocauan.authentication.config.TestContext;
import br.com.juliocauan.authentication.infrastructure.repository.RoleRepositoryImpl;
import br.com.juliocauan.authentication.infrastructure.repository.UserRepositoryImpl;

class EmailServiceTest extends TestContext {

    public EmailServiceTest(UserRepositoryImpl userRepository, RoleRepositoryImpl roleRepository,
            ObjectMapper objectMapper, MockMvc mockMvc) {
        super(userRepository, roleRepository, objectMapper, mockMvc);
    }

    @Test
    void sendEmail() {
        assertDoesNotThrow(() -> EmailUtil.sendEmail(getRandomUsername(), "Test Subject", "Test Message"));
    }

    @Test
    void sendEmail_error_mailSend() {
        MailSendException exception = assertThrowsExactly(MailSendException.class,
            () -> EmailUtil.sendEmail("null", "null", null));
        
        assertEquals("The recipient address is not a valid address!", exception.getMessage());
    }

}
