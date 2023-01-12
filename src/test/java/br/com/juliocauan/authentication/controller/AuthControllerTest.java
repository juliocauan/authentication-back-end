package br.com.juliocauan.authentication.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.HashSet;
import java.util.Set;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openapitools.model.EnumRole;
import org.openapitools.model.SignupForm;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.juliocauan.authentication.config.TestContext;
import br.com.juliocauan.authentication.infrastructure.model.UserEntity;
import br.com.juliocauan.authentication.infrastructure.repository.RoleRepositoryImpl;
import br.com.juliocauan.authentication.infrastructure.repository.UserRepositoryImpl;

public class AuthControllerTest extends TestContext {

    private final String urlSignup = "/api/auth/signup";
    private final String email = "test@email.com";
    private final String password = "1234567890";
    private final String username = "TestUsername";

    private final String emailNotPresent = "test2@email.com";
    private final String usernameNotPresent = "TestUsername2";

    private final String messageOk = "User registered successfully!";
    private final String errorDuplicatedUsername = "Username is already taken!";
    private final String errorDuplicatedEmail = "Email is already in use!";

    private final SignupForm signupForm = new SignupForm();

    private UserEntity entity;
    private Set<EnumRole> roles = new HashSet<>();

    public AuthControllerTest(UserRepositoryImpl userRepository, RoleRepositoryImpl roleRepository,
            ObjectMapper objectMapper, MockMvc mockMvc) {
        super(userRepository, roleRepository, objectMapper, mockMvc);
    }

    @Override @BeforeAll
    public void setup() {
        super.setup();
        getRoleRepository().findAll().forEach(role -> roles.add(role.getName()));
    }

    @BeforeEach
    public void standard(){
        getUserRepository().deleteAll();
        entity = UserEntity.builder()
            .email(email)
            .password(password)
            .username(username)
            .roles(null)
        .build();
        signupForm.email(email).password(password).username(username).roles(roles);
    }

    @Test
    public void givenValidSignupForm_WhenSignupUser_Then200AndMessage() throws Exception{
        getMockMvc().perform(
            post(urlSignup)
                .contentType(MediaType.APPLICATION_JSON)
                .content(getObjectMapper().writeValueAsString(signupForm)))
            .andDo(print())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").value(messageOk));
    }

    @Test
    public void givenDuplicatedEmailOrUsername_WhenSignupUser_ThenEntityExistsException() throws Exception{
        getUserRepository().save(entity);

        signupForm.email(emailNotPresent);
        getMockMvc().perform(
            post(urlSignup)
                .contentType(MediaType.APPLICATION_JSON)
                .content(getObjectMapper().writeValueAsString(signupForm)))
            .andDo(print())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value(errorDuplicatedUsername));
            
        signupForm.username(usernameNotPresent).email(email);
        getMockMvc().perform(
            post(urlSignup)
                .contentType(MediaType.APPLICATION_JSON)
                .content(getObjectMapper().writeValueAsString(signupForm)))
            .andDo(print())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value(errorDuplicatedEmail));
    }
    
}
