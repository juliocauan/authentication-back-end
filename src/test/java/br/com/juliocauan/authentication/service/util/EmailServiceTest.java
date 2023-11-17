package br.com.juliocauan.authentication.service.util;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailSendException;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.*;

import br.com.juliocauan.authentication.config.TestContext;
import br.com.juliocauan.authentication.domain.service.util.EmailService;
import br.com.juliocauan.authentication.infrastructure.repository.RoleRepositoryImpl;
import br.com.juliocauan.authentication.infrastructure.repository.UserRepositoryImpl;

class EmailServiceTest extends TestContext {

    private final EmailService emailService;

    //TODO refactor this email
    @Value("${test.mail.receiver}")
    private String receiver;
    private final String subject = "Test Subject";
    private final String message = "Test Message";
    
    private final String errorMailSend = "The recipient address is not a valid address!";

    public EmailServiceTest(UserRepositoryImpl userRepository, RoleRepositoryImpl roleRepository,
            ObjectMapper objectMapper, MockMvc mockMvc, EmailService emailService) {
        super(userRepository, roleRepository, objectMapper, mockMvc);
        this.emailService = emailService;
    }

    @Test
    void sendEmail() {
        assertDoesNotThrow(() -> emailService.sendEmail(receiver, subject, message));
    }

    @Test
    void sendEmail_error_mailSend() {
        MailSendException exception = assertThrowsExactly(MailSendException.class,
            () -> emailService.sendEmail("null", "null", null));
        
        assertEquals(errorMailSend, exception.getMessage());
    }

}
