package br.com.juliocauan.authentication.service.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailSendException;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.juliocauan.authentication.config.TestContext;
import br.com.juliocauan.authentication.infrastructure.repository.RoleRepositoryImpl;
import br.com.juliocauan.authentication.infrastructure.repository.UserRepositoryImpl;
import br.com.juliocauan.authentication.infrastructure.service.util.EmailService;

class EmailServiceTest extends TestContext {

    private final EmailService emailService;

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
    void givenValidArgs_whenSendEmail_ThenVoid() {
        Assertions.assertDoesNotThrow(() -> emailService.sendEmail(receiver, subject, message));
    }

    @Test
    void givenInvalidArgs_whenSendEmail_ThenMailException() {
        MailSendException exception = Assertions.assertThrowsExactly(MailSendException.class,
            () -> emailService.sendEmail("null", "null", null));
        
        Assertions.assertEquals(errorMailSend, exception.getMessage());
    }

}
