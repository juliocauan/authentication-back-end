package br.com.juliocauan.authentication.util;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.Test;
import org.openapitools.model.EmailType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.juliocauan.authentication.config.TestContext;
import br.com.juliocauan.authentication.infrastructure.repository.RoleRepositoryImpl;
import br.com.juliocauan.authentication.infrastructure.repository.UserRepositoryImpl;

class EmailServiceTest extends TestContext {

    private final EmailService emailService;
    private final String username = "admin@authentication.test";
    private final String password = "admin";

    public EmailServiceTest(UserRepositoryImpl userRepository, RoleRepositoryImpl roleRepository,
            ObjectMapper objectMapper, MockMvc mockMvc, EmailService emailService) {
        super(userRepository, roleRepository, objectMapper, mockMvc);
        this.emailService = emailService;
    }

    @Test
    void sendEmail() {
        emailService.setEmailer(username, password, EmailType.GREEN_MAIL);
        assertDoesNotThrow(() -> emailService.sendEmail(getRandomUsername(), "Test Subject", "Test Message"));
    }

}
