package br.com.juliocauan.authentication.model.mapper;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.juliocauan.authentication.config.TestContext;
import br.com.juliocauan.authentication.domain.model.RecoveryToken;
import br.com.juliocauan.authentication.domain.model.Role;
import br.com.juliocauan.authentication.domain.model.User;
import br.com.juliocauan.authentication.infrastructure.model.RecoveryTokenEntity;
import br.com.juliocauan.authentication.infrastructure.model.mapper.RecoveryTokenMapper;
import br.com.juliocauan.authentication.infrastructure.model.mapper.UserMapper;
import br.com.juliocauan.authentication.infrastructure.repository.RoleRepositoryImpl;
import br.com.juliocauan.authentication.infrastructure.repository.UserRepositoryImpl;

class RecoveryTokenMapperTest extends TestContext {

    private final Long idLong = 1L;
    private final String token = "testToken";
    private final LocalDateTime expireDate = LocalDateTime.now();
    private final UUID idUUID = UUID.randomUUID();
    private final String username = "test@email.com";
    private final String password = "1234567890";

    private Set<Role> roles = new HashSet<>();
    private RecoveryTokenEntity entity;

    public RecoveryTokenMapperTest(UserRepositoryImpl userRepository, RoleRepositoryImpl roleRepository,
            ObjectMapper objectMapper, MockMvc mockMvc) {
        super(userRepository, roleRepository, objectMapper, mockMvc);
    }

    private final User getUserDomain(){
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

    private final RecoveryToken getRecoveryToken() {
        return new RecoveryToken() {
            @Override
            public Long getId() {return idLong;}
            @Override
            public User getUser() {return getUserDomain();}
            @Override
            public String getToken() {return token;}
            @Override
            public LocalDateTime getExpireDate() {return expireDate;}
        };
    }

    private final RecoveryTokenEntity getRecoveryTokenEntity() {
        return RecoveryTokenEntity.builder()
            .id(idLong)
            .user(UserMapper.domainToEntity(getUserDomain()))
            .token(token)
            .expireDate(expireDate)
        .build();
    }

    @BeforeEach
    public void standard(){
        entity = getRecoveryTokenEntity();
    }
    
    @Test
    void domainToEntity() {
        RecoveryTokenEntity mappedEntity = RecoveryTokenMapper.domainToEntity(getRecoveryToken());
        Assertions.assertEquals(entity, mappedEntity);
    }

}
