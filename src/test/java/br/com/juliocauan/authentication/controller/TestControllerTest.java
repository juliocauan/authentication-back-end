package br.com.juliocauan.authentication.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.juliocauan.authentication.config.TestContext;
import br.com.juliocauan.authentication.infrastructure.repository.RoleRepositoryImpl;
import br.com.juliocauan.authentication.infrastructure.repository.UserRepositoryImpl;

public class TestControllerTest extends TestContext {

    private final String urlAll = "/api/test/all";
    private final String urlAdmin = "/api/test/admin";
    private final String urlManager = "/api/test/manager";
    private final String urlUser = "/api/test/user";

    private final String allOkMessage = "Public Content";
    private final String adminOkMessage = "Admin Board";
    private final String managerOkMessage = "Manager Board";
    private final String userOkMessage = "User Board";
    private final String notAllowedMessage = "Not Allowed!";

    public TestControllerTest(UserRepositoryImpl userRepository, RoleRepositoryImpl roleRepository,
            ObjectMapper objectMapper, MockMvc mockMvc) {
        super(userRepository, roleRepository, objectMapper, mockMvc);
    }

    @Test
    public void givenNonAuthenticatedUser_WhenAllAccess_ThenOkMessage() throws Exception {
        getMockMvc().perform(
                get(urlAll))
                .andDo(print())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(allOkMessage));
    }

    @Test
    @WithMockUser(authorities = {"ADMIN"})
    public void givenAuthenticatedAdmin_WhenAdminAccess_ThenAdminOkMessage() throws Exception {
        getMockMvc().perform(
                get(urlAdmin))
                .andDo(print())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(adminOkMessage));
    }

    @Test
    @WithMockUser(authorities = {"USER", "MANAGER"})
    public void givenAuthenticatedUserOrManager_WhenAdminAccess_ThenForbidden() throws Exception {
        getMockMvc().perform(
                get(urlAdmin))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(authorities = {"MANAGER"})
    public void givenAuthenticatedManager_WhenManagerAccess_ThenManagerOkMessage() throws Exception {
        getMockMvc().perform(
                get(urlManager))
                .andDo(print())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(managerOkMessage));
    }

    @Test
    @WithMockUser(authorities = {"USER", "ADMIN"})
    public void givenAuthenticatedUserOrAdmin_WhenManagerAccess_ThenForbidden() throws Exception {
        getMockMvc().perform(
                get(urlManager))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(authorities = {"USER"})
    public void givenAuthenticatedUser_WhenUserAccess_ThenUserOkMessage() throws Exception {
        getMockMvc().perform(
                get(urlUser))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(userOkMessage));
    }

    @Test
    @WithMockUser(authorities = {"MANAGER", "ADMIN"})
    public void givenAuthenticatedManagerOrAdmin_WhenUserAccess_ThenForbidden() throws Exception {
        getMockMvc().perform(
                get(urlUser))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    public void givenNonAuthenticated_WhenUserAccess_ThenUnauthorized() throws Exception {
        getMockMvc().perform(
                get(urlUser))
                .andDo(print())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value(notAllowedMessage));
    }

}
