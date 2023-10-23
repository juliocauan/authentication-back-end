package br.com.juliocauan.authentication.model.mapper;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openapitools.model.EnumRole;
import org.openapitools.model.SignupForm;
import org.openapitools.model.UserInfo;
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
import br.com.juliocauan.authentication.infrastructure.security.model.UserPrincipal;

class UserMapperTest extends TestContext {

    private final PasswordEncoder encoder;

    private final UUID idUUID = UUID.randomUUID();
    private final Short idShort = 1;
    private final String username = "test@email.com";
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
            public UUID getId() {return idUUID;}
            @Override
            public String getUsername() {return username;}
            @Override
            public String getPassword() {return password;}
            @Override
            public Set<Role> getRoles() {return roles;}
        };
    }
    private final UserEntity getUserEntity(){
        return UserEntity.builder()
            .id(idUUID)
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
        entity = getUserEntity();
    }

    @Test
    void domainToEntity(){
        UserEntity mappedEntity = UserMapper.domainToEntity(getUser());
        Assertions.assertEquals(entity, mappedEntity);
    }

    @Test
    void signupFormToEntity(){
        SignupForm signupForm = new SignupForm();
        signupForm.username(username).password(password);
        UserEntity mappedEntity = UserMapper.signupFormToEntity(signupForm, roleEntities, encoder);
        Assertions.assertEquals(null, mappedEntity.getId());
        Assertions.assertNotEquals(entity.getPassword(), mappedEntity.getPassword());
        Assertions.assertEquals(60, mappedEntity.getPassword().length());
        Assertions.assertEquals(entity.getUsername(), mappedEntity.getUsername());
        Assertions.assertEquals(entity.getRoles(), mappedEntity.getRoles());
    }

    @Test
    void domainToUserPrincipal() {
        UserPrincipal userPrincipal = UserMapper.domainToUserPrincipal(getUser());
        Assertions.assertEquals(getUser().getUsername(), userPrincipal.getUsername());
        Assertions.assertEquals(getUser().getPassword(), userPrincipal.getPassword());
        Assertions.assertEquals(getUser().getRoles().size(), userPrincipal.getAuthorities().size());
    }

    @Test
    void domainToUserInfo() {
        UserInfo userInfo = UserMapper.domainToUserInfo(getUser());
        Assertions.assertEquals(getUser().getId(), userInfo.getId());
        Assertions.assertEquals(getUser().getUsername(), userInfo.getUsername());
        Assertions.assertEquals(getUser().getRoles().size(), userInfo.getRoles().size());
    }

    @Test
    void entityToDomain() {
        User user = UserMapper.entityToDomain(entity);
        Assertions.assertEquals(user.getId(), getUser().getId());
        Assertions.assertEquals(user.getUsername(), getUser().getUsername());
        Assertions.assertEquals(user.getPassword(), getUser().getPassword());
        Assertions.assertEquals(user.getRoles().size(), getUser().getRoles().size());
    }

}
