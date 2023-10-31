package br.com.juliocauan.authentication.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.HashSet;
import java.util.Set;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openapitools.model.EnumRole;
import org.openapitools.model.JWTResponse;
import org.openapitools.model.UpdateUserRolesForm;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.juliocauan.authentication.config.TestContext;
import br.com.juliocauan.authentication.infrastructure.model.RoleEntity;
import br.com.juliocauan.authentication.infrastructure.model.UserEntity;
import br.com.juliocauan.authentication.infrastructure.repository.RoleRepositoryImpl;
import br.com.juliocauan.authentication.infrastructure.repository.UserRepositoryImpl;
import br.com.juliocauan.authentication.infrastructure.service.application.AuthenticationServiceImpl;

class AdminControllerTest extends TestContext {

    private final AuthenticationServiceImpl authenticationService;
    private final PasswordEncoder encoder;

    private final String urlAdmin = "/api/auth/admin";
    private final String authorizationHeader = "Authorization";

    private final String usernameAdmin = "admin@email.com";
    private final String usernameManager = "manager@email.com";
    private final String usernameUser = "user@email.com";
    private final String password = "1234567890";
    private final int uuidSize = 36;
    
    private final String okAlterUserRole = "Patched user roles successfully!";
    private final String errorUserNotFound = "User Not Found with username: ";
    private final String errorNotAuthorized = "Full authentication is required to access this resource";

    public AdminControllerTest(UserRepositoryImpl userRepository, RoleRepositoryImpl roleRepository,
            ObjectMapper objectMapper, MockMvc mockMvc, AuthenticationServiceImpl authenticationService, PasswordEncoder encoder) {
        super(userRepository, roleRepository, objectMapper, mockMvc);
        this.authenticationService = authenticationService;
        this.encoder = encoder;
    }

    @BeforeEach
    void standard(){
        getUserRepository().deleteAll();
        buildAndSaveUser(usernameAdmin, EnumRole.ADMIN);
    }

    private final void buildAndSaveUser(String username, EnumRole enumRole) {
        Set<RoleEntity> roles = new HashSet<>();
        roles.add( new RoleEntity(getRoleRepository().getByName(enumRole).get()) );
        getUserRepository().save(UserEntity
            .builder()
                .id(null)
                .username(username)
                .password(encoder.encode(password))
                .roles(roles)
            .build());
    }

    private final String getToken(String username){
        JWTResponse jwt = authenticationService.authenticate(username, password);
        return jwt.getType() + " " + jwt.getToken();
    }

    private final Set<EnumRole> getRoles() {
        Set<EnumRole> roles = new HashSet<>();
        for(EnumRole role : EnumRole.values()) roles.add(role);
        return roles;
    }

    @Test
    void updateUserRole() throws Exception{
        buildAndSaveUser(usernameManager, EnumRole.MANAGER);
        UpdateUserRolesForm updateUserRolesForm = new UpdateUserRolesForm().username(usernameManager).roles(getRoles());
        getMockMvc().perform(
            patch(urlAdmin)
                .header(authorizationHeader, getToken(usernameAdmin))
                .contentType(MediaType.APPLICATION_JSON)
                .content(getObjectMapper().writeValueAsString(updateUserRolesForm)))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.body").value(okAlterUserRole));
    }

    @Test
    void updateUserRole_error_unauthorized() throws Exception{
        getMockMvc().perform(
            patch(urlAdmin))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.message").value(errorNotAuthorized));
    }

    @Test
    void updateUserRole_error_forbidden() throws Exception{
        UpdateUserRolesForm updateUserRolesForm = new UpdateUserRolesForm().username(usernameManager).roles(getRoles());
        buildAndSaveUser(usernameManager, EnumRole.MANAGER);
        getMockMvc().perform(
            patch(urlAdmin)
                .header(authorizationHeader, getToken(usernameManager))
                .contentType(MediaType.APPLICATION_JSON)
                .content(getObjectMapper().writeValueAsString(updateUserRolesForm)))
            .andExpect(status().isForbidden());
        
        updateUserRolesForm.username(usernameUser);
        buildAndSaveUser(usernameUser, EnumRole.USER);
        getMockMvc().perform(
            patch(urlAdmin)
                .header(authorizationHeader, getToken(usernameUser))
                .contentType(MediaType.APPLICATION_JSON)
                .content(getObjectMapper().writeValueAsString(updateUserRolesForm)))
            .andExpect(status().isForbidden());
    }

    @Test
    void updateUserRole_error_usernameNotFound() throws Exception{
        UpdateUserRolesForm updateUserRolesForm = new UpdateUserRolesForm().username(usernameUser).roles(getRoles());
        getMockMvc().perform(
            patch(urlAdmin)
                .header(authorizationHeader, getToken(usernameAdmin))
                .contentType(MediaType.APPLICATION_JSON)
                .content(getObjectMapper().writeValueAsString(updateUserRolesForm)))
            .andExpect(status().isNotFound())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.message").value(errorUserNotFound + usernameUser))
            .andExpect(jsonPath("$.timestamp").isNotEmpty())
            .andExpect(jsonPath("$.fieldErrors").isEmpty());
    }

    @Test
    void getAllUsers() throws Exception{
        buildAndSaveUser(usernameUser, EnumRole.USER);
        buildAndSaveUser(usernameManager, EnumRole.MANAGER);
        getMockMvc().perform(
            get(urlAdmin)
                .header(authorizationHeader, getToken(usernameAdmin)))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$", hasSize(3)))
            .andExpect(jsonPath("$.[0].id", Matchers.hasLength(uuidSize)))
            .andExpect(jsonPath("$.[0].username", Matchers.containsString("@email.com")))
            .andExpect(jsonPath("$.[0].roles", hasSize(1)));
    }

    @Test
    void getAllUsers_branch_username() throws Exception{
        buildAndSaveUser(usernameUser, EnumRole.USER);
        buildAndSaveUser(usernameManager, EnumRole.MANAGER);
        getMockMvc().perform(
            get(urlAdmin)
                .header(authorizationHeader, getToken(usernameAdmin))
                .queryParam("username", "manag"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$.[0].id", Matchers.hasLength(uuidSize)))
            .andExpect(jsonPath("$.[0].username").value(usernameManager))
            .andExpect(jsonPath("$.[0].roles[0]").value(EnumRole.MANAGER.getValue()));
    }

    @Test
    void getAllUsers_branch_role() throws Exception{
        buildAndSaveUser(usernameUser, EnumRole.USER);
        buildAndSaveUser(usernameManager, EnumRole.MANAGER);
        getMockMvc().perform(
            get(urlAdmin)
                .header(authorizationHeader, getToken(usernameAdmin))
                .queryParam("role", EnumRole.USER.getValue()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$.[0].id", Matchers.hasLength(uuidSize)))
            .andExpect(jsonPath("$.[0].username").value(usernameUser))
            .andExpect(jsonPath("$.[0].roles[0]").value(EnumRole.USER.getValue()));
    }

    @Test
    void getAllUsers_branch_usernameAndRole() throws Exception{
        buildAndSaveUser(usernameUser, EnumRole.USER);
        buildAndSaveUser(usernameManager, EnumRole.MANAGER);
        getMockMvc().perform(
            get(urlAdmin)
                .header(authorizationHeader, getToken(usernameAdmin))
                .queryParam("username", "admin")
                .queryParam("role", EnumRole.USER.getValue()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void getAllUsers_error_unauthorized() throws Exception{
        getMockMvc().perform(
            get(urlAdmin))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.message").value(errorNotAuthorized));
    }

    @Test
    void getAllUsers_error_forbidden() throws Exception{
        buildAndSaveUser(usernameManager, EnumRole.MANAGER);
        getMockMvc().perform(
            get(urlAdmin)
                .header(authorizationHeader, getToken(usernameManager)))
            .andExpect(status().isForbidden());

        buildAndSaveUser(usernameUser, EnumRole.USER);
        getMockMvc().perform(
            get(urlAdmin)
                .header(authorizationHeader, getToken(usernameUser)))
            .andExpect(status().isForbidden());
    }
    
}
