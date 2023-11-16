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
    private final String password = RandomGenerator.getDefault().toString();

    @BeforeAll
    public void setup(){
        userRepository.deleteAll();
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

    public String getPassword() {
        return password;
    }
    
}