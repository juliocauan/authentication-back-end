package br.com.juliocauan.authentication.config;

import java.util.random.RandomGenerator;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestConstructor;
import org.springframework.test.context.TestConstructor.AutowireMode;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.icegreen.greenmail.configuration.GreenMailConfiguration;
import com.icegreen.greenmail.junit5.GreenMailExtension;
import com.icegreen.greenmail.util.ServerSetupTest;

import br.com.juliocauan.authentication.infrastructure.model.RoleEntity;
import br.com.juliocauan.authentication.infrastructure.repository.RoleRepositoryImpl;
import br.com.juliocauan.authentication.infrastructure.repository.UserRepositoryImpl;
import lombok.AllArgsConstructor;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(Lifecycle.PER_CLASS)
@TestConstructor(autowireMode = AutowireMode.ALL)
@AllArgsConstructor
public class TestContext {
    
    @RegisterExtension
    static GreenMailExtension greenMail = new GreenMailExtension(ServerSetupTest.SMTP)
        .withConfiguration(GreenMailConfiguration.aConfig().withUser("admin@authentication.test", "admin"))
        .withPerMethodLifecycle(false);

    private final UserRepositoryImpl userRepository;
    private final RoleRepositoryImpl roleRepository;
    private final ObjectMapper objectMapper;
    private final MockMvc mockMvc;
    private final RandomGenerator randomGenerator = RandomGenerator.getDefault();

    private final String characters = "ABCDEFGHIJKLMnopqrstuvwxyz0123456789";

    @BeforeAll
    public void setup(){
        userRepository.deleteAll();
        roleRepository.deleteAll();
        roleRepository.save(RoleEntity.builder().name("ADMIN").build());
    }

    public MockMvc getMockMvc() {
        return mockMvc;
    }

    public ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    public UserRepositoryImpl getUserRepository() {
        return userRepository;
    }
    
    public RoleRepositoryImpl getRoleRepository() {
        return roleRepository;
    }

    public String getRandomUsername() {
        return getRandomString(12) + "@email.test";
    }

    public String getRandomPassword() {
        return getRandomString(4) + "aB3$";
    }

    public String getRandomToken() {
        return getRandomString(43);
    }

    public String getErrorUsernameNotFound(String username) {
        return String.format("Username [%s] not found!", username);
    }

    public String getErrorDuplicatedUsername(String username) {
        return String.format("Username [%s] is already taken!", username);
    }

    private String getRandomString(Integer length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int index = randomGenerator.nextInt(characters.length());
            sb.append(characters.charAt(index));
        }
        return sb.toString();
    }
    
}