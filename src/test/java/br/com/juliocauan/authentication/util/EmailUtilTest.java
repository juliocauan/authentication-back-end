package br.com.juliocauan.authentication.util;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.Test;
import org.openapitools.model.EmailType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.juliocauan.authentication.config.TestContext;
import br.com.juliocauan.authentication.infrastructure.repository.RoleRepository;
import br.com.juliocauan.authentication.infrastructure.repository.UserRepositoryImpl;

class EmailUtilTest extends TestContext {

    public EmailUtilTest(UserRepositoryImpl userRepository, RoleRepository roleRepository,
            ObjectMapper objectMapper, MockMvc mockMvc) {
        super(userRepository, roleRepository, objectMapper, mockMvc);
        EmailUtil.setEmailer("admin@authentication.test", "admin", EmailType.GREEN_MAIL);
    }

    @Test
    void sendEmail() {
        assertDoesNotThrow(() -> EmailUtil.sendEmail(getRandomUsername(), "Test Subject", "Test Message"));
    }

}
