package br.com.juliocauan.authentication.repository;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openapitools.model.EnumRole;

import br.com.juliocauan.authentication.config.TestContext;
import br.com.juliocauan.authentication.infrastructure.model.RoleEntity;
import br.com.juliocauan.authentication.infrastructure.model.UserEntity;
import br.com.juliocauan.authentication.infrastructure.repository.RoleRepositoryImpl;
import br.com.juliocauan.authentication.infrastructure.repository.UserRepositoryImpl;

public class UserRepositoryTest extends TestContext {
    
    private final UserRepositoryImpl userRepository;
    private final RoleRepositoryImpl roleRepository;

    private final String password = "12345678";
    private final String username = "testUsername";
    private final String email = "test@email.com";

    private UserEntity entity;
    private Set<RoleEntity> roles = new HashSet<>();

    public UserRepositoryTest(UserRepositoryImpl userRepository, RoleRepositoryImpl roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    @BeforeAll
    public void setup(){
        userRepository.deleteAll();
        roleRepository.deleteAll();
        for(EnumRole name : EnumRole.values())
            roleRepository.save(RoleEntity.builder().name(name).build());
        roleRepository.findAll().forEach(role -> roles.add(role));
    }

    @BeforeEach
    public void standard(){
        userRepository.deleteAll();
        entity = UserEntity.builder()
            .email(email)
            .password(password)
            .username(username)
            .roles(roles)
        .build();
    }

    @Test
    public void givenPresentUsername_WhenExistsByUsername_ThenTrue(){
        userRepository.save(entity);
        Assertions.assertTrue(userRepository.existsByUsername(username));
    }

    @Test
    public void givenNotPresentUsername_WhenExistsByUsername_ThenFalse(){
        Assertions.assertFalse(userRepository.existsByUsername(username));
    }

    @Test
    public void givenPresentEmail_WhenExistsByEmail_ThenTrue(){
        userRepository.save(entity);
        Assertions.assertTrue(userRepository.existsByEmail(email));
    }

    @Test
    public void givenNotPresentEmail_WhenExistsByEmail_ThenFalse(){
        Assertions.assertFalse(userRepository.existsByEmail(email));
    }

    @Test
    public void givenPresentUsername_WhenFindByUsername_ThenUser(){
        userRepository.save(entity);
        Assertions.assertEquals(entity, userRepository.findByUsername(username).get());
    }

    @Test
    public void givenNotPresentUsername_WhenFindByUsername_ThenUserNotPresent(){
        Assertions.assertFalse(userRepository.findByUsername(username).isPresent());
    }

}
