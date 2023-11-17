package br.com.juliocauan.authentication.model.mapper;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.openapitools.model.UserInfo;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.juliocauan.authentication.config.TestContext;
import br.com.juliocauan.authentication.domain.model.Role;
import br.com.juliocauan.authentication.domain.model.User;
import br.com.juliocauan.authentication.infrastructure.model.mapper.UserMapper;
import br.com.juliocauan.authentication.infrastructure.repository.RoleRepositoryImpl;
import br.com.juliocauan.authentication.infrastructure.repository.UserRepositoryImpl;
import br.com.juliocauan.authentication.infrastructure.security.model.UserPrincipal;

class UserMapperTest extends TestContext {

    private final String username = getRandomUsername();
    private final String password = getRandomPassword();

    public UserMapperTest(UserRepositoryImpl userRepository, RoleRepositoryImpl roleRepository,
            ObjectMapper objectMapper, MockMvc mockMvc) {
        super(userRepository, roleRepository, objectMapper, mockMvc);
    }
    
    private final User getUser(){
        return new User() {
            @Override
            public Integer getId() {return 1;}
            @Override
            public String getUsername() {return username;}
            @Override
            public String getPassword() {return password;}
            @Override
            public Set<Role> getRoles() {return new HashSet<>();}
        };
    }

    @Test
    void domainToUserPrincipal() {
        UserPrincipal userPrincipal = UserMapper.domainToUserPrincipal(getUser());
        assertEquals(getUser().getUsername(), userPrincipal.getUsername());
        assertEquals(getUser().getPassword(), userPrincipal.getPassword());
        assertEquals(getUser().getRoles().size(), userPrincipal.getAuthorities().size());
    }

    @Test
    void domainToUserInfo() {
        UserInfo userInfo = UserMapper.domainToUserInfo(getUser());
        assertEquals(getUser().getId(), userInfo.getId());
        assertEquals(getUser().getUsername(), userInfo.getUsername());
        assertEquals(getUser().getRoles().size(), userInfo.getRoles().size());
    }

}
