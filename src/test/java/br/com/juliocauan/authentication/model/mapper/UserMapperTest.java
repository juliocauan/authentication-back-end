package br.com.juliocauan.authentication.model.mapper;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openapitools.model.EnumRole;
import org.openapitools.model.SignupForm;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.juliocauan.authentication.config.TestContext;
import br.com.juliocauan.authentication.domain.model.Role;
import br.com.juliocauan.authentication.domain.model.User;
import br.com.juliocauan.authentication.infrastructure.model.RoleEntity;
import br.com.juliocauan.authentication.infrastructure.model.UserEntity;
import br.com.juliocauan.authentication.infrastructure.model.mapper.UserMapper;
import br.com.juliocauan.authentication.infrastructure.repository.RoleRepositoryImpl;
import br.com.juliocauan.authentication.infrastructure.repository.UserRepositoryImpl;

public class UserMapperTest extends TestContext {

    private final PasswordEncoder encoder;

    private final Long idLong = 1L;
    private final Short idShort = 1;
    private final String username = "usernameTest";
    private final String email = "email@test.com";
    private final String password = "1234567890";

    private UserEntity entity;
    private Set<RoleEntity> roleEntities = new HashSet<>();
    private Set<Role> roles = new HashSet<>();

    public UserMapperTest(UserRepositoryImpl userRepository, RoleRepositoryImpl roleRepository,
            ObjectMapper objectMapper, MockMvc mockMvc, PasswordEncoder encoder) {
        super(userRepository, roleRepository, objectMapper, mockMvc);
        this.encoder = encoder;
    }

    private final Role getRole(EnumRole role){
        return new Role() {
            @Override
            public Short getId() {return idShort;}
            @Override
            public EnumRole getName() {return role;}
        };
    }
    private final User getUser(){
        return new User() {
            @Override
            public Long getId() {return idLong;}
            @Override
            public String getUsername() {return username;}
            @Override
            public String getEmail() {return email;}
            @Override
            public String getPassword() {return password;}
            @Override
            public Set<Role> getRoles() {return roles;}
        };
    }
    private final UserEntity getUserEntity(){
        return UserEntity.builder()
            .id(idLong)
            .email(email)
            .password(password)
            .username(username)
            .roles(roleEntities)
        .build();
    }

    @Override @BeforeAll
    public void setup() {
        super.setup();
        roleEntities = getRoleRepository().findAll().stream().collect(Collectors.toSet());
        for(EnumRole name : EnumRole.values()) roles.add(getRole(name));
    }
    @BeforeEach
    public void standard(){
        entity = getUserEntity();
    }

    @Test
    public void domainToEntity(){
        UserEntity mappedEntity = UserMapper.domainToEntity(getUser());
        Assertions.assertEquals(entity, mappedEntity);
    }

    @Test
    public void formToEntity(){
        SignupForm signupForm = new SignupForm();
        signupForm.email(email).username(username).password(password).roles(null);
        UserEntity mappedEntity = UserMapper.formToEntity(signupForm, roleEntities, encoder);
        Assertions.assertEquals(null, mappedEntity.getId());
        Assertions.assertEquals(entity.getEmail(), mappedEntity.getEmail());
        Assertions.assertNotEquals(entity.getPassword(), mappedEntity.getPassword());
        Assertions.assertEquals(60, mappedEntity.getPassword().length());
        Assertions.assertEquals(entity.getUsername(), mappedEntity.getUsername());
        Assertions.assertEquals(entity.getRoles(), mappedEntity.getRoles());
    }

}
