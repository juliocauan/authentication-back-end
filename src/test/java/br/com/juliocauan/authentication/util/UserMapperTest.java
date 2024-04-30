package br.com.juliocauan.authentication.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.openapitools.model.UserInfo;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.juliocauan.authentication.config.TestContext;
import br.com.juliocauan.authentication.domain.model.User;
import br.com.juliocauan.authentication.infrastructure.repository.RoleRepository;
import br.com.juliocauan.authentication.infrastructure.repository.UserRepository;
import br.com.juliocauan.authentication.infrastructure.security.model.UserPrincipal;

class UserMapperTest extends TestContext {

    private final String username = getRandomUsername();
    private final String password = getRandomPassword();

    public UserMapperTest(UserRepository userRepository, RoleRepository roleRepository,
            ObjectMapper objectMapper, MockMvc mockMvc) {
        super(userRepository, roleRepository, objectMapper, mockMvc);
    }
    
    private final User getUser(){
        User user = new User(username, password);
        user.setId(1);
        return user;
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
