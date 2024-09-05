package br.com.juliocauan.authentication.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.openapitools.model.UserInfo;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.juliocauan.authentication.config.TestContext;
import br.com.juliocauan.authentication.domain.model.User;
import br.com.juliocauan.authentication.infrastructure.repository.RoleRepository;
import br.com.juliocauan.authentication.infrastructure.repository.UserRepository;
import br.com.juliocauan.authentication.util.mapper.UserMapper;

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
    void domainToUserDetails() {
        User user = getUser();
        UserDetails userPrincipal = UserMapper.domainToUserDetails(user);
        assertEquals(user.getUsername(), userPrincipal.getUsername());
        assertEquals(user.getPassword(), userPrincipal.getPassword());
        assertEquals(user.getRoles().size(), userPrincipal.getAuthorities().size());
        assertEquals(user.isDisabled(), !userPrincipal.isEnabled());
        assertEquals(user.isLocked(), !userPrincipal.isAccountNonLocked());
    }

    @Test
    void domainToUserInfo() {
        User user = getUser();
        UserInfo userInfo = UserMapper.domainToUserInfo(user);
        assertEquals(user.getId(), userInfo.getId());
        assertEquals(user.getUsername(), userInfo.getUsername());
        assertEquals(user.getRoles().size(), userInfo.getRoles().size());
    }

}
