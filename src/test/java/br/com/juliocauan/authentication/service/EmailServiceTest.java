package br.com.juliocauan.authentication.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.mail.MailException;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.juliocauan.authentication.config.TestContext;
import br.com.juliocauan.authentication.infrastructure.repository.RoleRepositoryImpl;
import br.com.juliocauan.authentication.infrastructure.repository.UserRepositoryImpl;
import br.com.juliocauan.authentication.infrastructure.service.EmailServiceImpl;

class EmailServiceTest extends TestContext {

    private final EmailServiceImpl emailService;

    //TODO review this email
    private final String receiver = "jcam.test01@gmail.com";
    private final String subject = "Test Subject";
    private final String message = "Test Message";

    public EmailServiceTest(UserRepositoryImpl userRepository, RoleRepositoryImpl roleRepository,
            ObjectMapper objectMapper, MockMvc mockMvc, EmailServiceImpl emailService) {
        super(userRepository, roleRepository, objectMapper, mockMvc);
        this.emailService = emailService;
    }

    @Test
    void givenValidArgs_whenSendEmail_ThenVoid() {
        Assertions.assertDoesNotThrow(() -> emailService.sendEmail(receiver, subject, message));
    }

    @Test
    void givenInvalidArgs_whenSendEmail_ThenMailException() {
        Assertions.assertThrows(MailException.class, () -> emailService.sendEmail("null", "null", null));
    }

}
