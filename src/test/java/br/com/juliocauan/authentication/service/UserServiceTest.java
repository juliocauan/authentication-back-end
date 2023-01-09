package br.com.juliocauan.authentication.service;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.juliocauan.authentication.config.TestContext;
import br.com.juliocauan.authentication.domain.repository.UserRepository;
import br.com.juliocauan.authentication.infrastructure.model.RoleEntity;
import br.com.juliocauan.authentication.infrastructure.model.UserEntity;
import br.com.juliocauan.authentication.infrastructure.repository.RoleRepositoryImpl;
import br.com.juliocauan.authentication.infrastructure.repository.UserRepositoryImpl;
import br.com.juliocauan.authentication.infrastructure.service.UserServiceImpl;
import jakarta.persistence.EntityExistsException;
import jakarta.validation.ConstraintViolationException;

public class UserServiceTest extends TestContext {

    private final UserServiceImpl userService;

    private final String password = "12345678";
    private final String username = "testUsername";
    private final String email = "test@email.com";

    private final String invalidUsernameMin = "abcde";
    private final String invalidUsernameMax = "abcdefghijklmnopqrstu";
    private final String invalidEmailMax = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa";
    private final String invalidEmailBlank = "     ";
    private final String invalidPasswordMin = "1234567";
    private final String invalidPasswordMax = "1111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111";
    private final String invalidPasswordBlank = "    ";

    private final String errorUsernameNotFound =  "User Not Found with username: " + username;
    private final String errorDuplicatedUsername = "Username is already taken!";
    private final String errorDuplicatedEmail = "Email is already taken!";

    private UserEntity entity;
    private Set<RoleEntity> roles = new HashSet<>();

    public UserServiceTest(UserRepositoryImpl userRepository, RoleRepositoryImpl roleRepository,
            ObjectMapper objectMapper, MockMvc mockMvc, UserServiceImpl userService) {
        super(userRepository, roleRepository, objectMapper, mockMvc);
        this.userService = userService;
    }

    @Override @BeforeAll
    public void setup(){
        super.setup();
        getRoleRepository().findAll().forEach(role -> roles.add(role));
    }

    @BeforeEach
    public void standard(){
        getUserRepository().deleteAll();
        entity = UserEntity.builder()
            .email(email)
            .password(password)
            .username(username)
            .roles(roles)
        .build();
    }

    @Test
    public void whenGetRepository_ThenInstanceOfUserRepository(){
        Assertions.assertInstanceOf(UserRepository.class, userService.getRepository());
    }

    @Test
    public void givenPresentUsername_WhenGetByUsername_ThenEqualsUser(){
        getUserRepository().save(entity);
        Assertions.assertEquals(entity, userService.getByUsername(username));
    }

    @Test
    public void givenNotPresentUsername_WhenGetByUsername_ThenUsernameNotFoundException(){
        Assertions.assertThrows(UsernameNotFoundException.class, () -> userService.getByUsername(username), errorUsernameNotFound);
    }

    @Test
    public void givenDuplicatedUsername_WhenCheckDuplicatedUsername_ThenEntityExistsException(){
        getUserRepository().save(entity);
        Assertions.assertThrows(EntityExistsException.class, () -> userService.checkDuplicatedUsername(username), errorDuplicatedUsername);
    }

    @Test
    public void givenNotDuplicatedUsername_WhenCheckDuplicatedUsername_ThenVoid(){
        Assertions.assertDoesNotThrow(() -> userService.checkDuplicatedUsername(username));
    }

    @Test
    public void givenDuplicatedEmail_WhenCheckDuplicatedEmail_ThenEntityExistsException(){
        getUserRepository().save(entity);
        Assertions.assertThrows(EntityExistsException.class, () -> userService.checkDuplicatedEmail(email), errorDuplicatedEmail);
    }

    @Test
    public void givenNotDuplicatedEmail_WhenCheckDuplicatedEmail_ThenVoid(){
        Assertions.assertDoesNotThrow(() -> userService.checkDuplicatedEmail(email));
    }

    @Test
    public void givenPresentUsername_WhenLoadUserByUsername_ThenUser(){
        getUserRepository().save(entity);
        Assertions.assertEquals(entity, userService.loadUserByUsername(username));
    }

    @Test
    public void givenNotPresentUsername_WhenLoadUserByUsername_ThenUsernameNotFoundException(){
        Assertions.assertThrows(UsernameNotFoundException.class, () -> userService.loadUserByUsername(username), errorUsernameNotFound);
    }

    @Test
    public void givenValidUserEntity_WhenSave_ThenVoid(){
        Assertions.assertDoesNotThrow(() -> userService.save(entity));
    }

    @Test
    public void givenInvalidFields_WhenSave_ThenConstraintViolationException(){
        entity.setUsername(invalidUsernameMin);
        Assertions.assertThrows(ConstraintViolationException.class, () -> userService.save(entity));

        entity.setUsername(invalidUsernameMax);
        Assertions.assertThrows(ConstraintViolationException.class, () -> userService.save(entity));

        entity.setUsername(username);

        entity.setEmail(invalidEmailMax);
        Assertions.assertThrows(ConstraintViolationException.class, () -> userService.save(entity));

        entity.setEmail(invalidEmailBlank);
        Assertions.assertThrows(ConstraintViolationException.class, () -> userService.save(entity));

        entity.setEmail(email);

        entity.setPassword(invalidPasswordBlank);
        Assertions.assertThrows(ConstraintViolationException.class, () -> userService.save(entity));
        
        entity.setPassword(invalidPasswordMax);
        Assertions.assertThrows(ConstraintViolationException.class, () -> userService.save(entity));
        
        entity.setPassword(invalidPasswordMin);
        Assertions.assertThrows(ConstraintViolationException.class, () -> userService.save(entity)).printStackTrace();;
    }
    
}
