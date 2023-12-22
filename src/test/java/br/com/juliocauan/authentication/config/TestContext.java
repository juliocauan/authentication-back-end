package br.com.juliocauan.authentication.config;

import java.util.random.RandomGenerator;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestConstructor;
import org.springframework.test.context.TestConstructor.AutowireMode;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

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

    private final UserRepositoryImpl userRepository;
    private final RoleRepositoryImpl roleRepository;
    private final ObjectMapper objectMapper;
    private final MockMvc mockMvc;
    private final String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private final RandomGenerator randomGenerator = RandomGenerator.getDefault();

    @BeforeAll
    public void beforeAll() {
        userRepository.deleteAll();
        roleRepository.deleteAll();
        roleRepository.save(RoleEntity.builder().name("ADMIN").build());
    }

    public MockMvc getMockMvc() {
        return mockMvc;
    }

    public String writeValueAsString(Object value) throws Exception {
        return objectMapper.writeValueAsString(value);
    }

    public UserRepositoryImpl getUserRepository() {
        return userRepository;
    }

    public RoleRepositoryImpl getRoleRepository() {
        return roleRepository;
    }

    public String getRandomUsername() {
        return getRandomUsername(null);
    }

    public String getRandomUsername(Integer length) {
        length = (length == null || length <= 11) ? 5 : (length - 11);
        return getRandomString(length) + "@email.test";
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

    public String getErrorUsernameDuplicated(String username) {
        return String.format("Username [%s] is already taken!", username);
    }

    public String getErrorPasswordResetNotFound(String token) {
        return String.format("Token [%s] not found!", token);
    }

    public String getRandomString(Integer length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int index = randomGenerator.nextInt(characters.length());
            sb.append(characters.charAt(index));
        }
        return sb.toString();
    }

}