package br.com.juliocauan.authentication.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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

    private final String url = "/api/auth/admin/users";
    private final String authorizationHeader = "Authorization";

    private final String password = getRandomPassword();
    private final String usernameAdmin = "admin@email.com";
    private final String usernameManager = "manager@email.com";
    private final String usernameUser = "user@email.com";
    
    private final String okAlterUserRole = "Patched user roles successfully!";
    private final String errorUserNotFound = "User Not Found with username: ";
    private final String errorNotAuthorized = "Full authentication is required to access this resource";

    private final String roleManager = "MANAGER";
    private final String roleUser = "USER";

    public AdminControllerTest(UserRepositoryImpl userRepository, RoleRepositoryImpl roleRepository,
            ObjectMapper objectMapper, MockMvc mockMvc, AuthenticationServiceImpl authenticationService, PasswordEncoder encoder) {
        super(userRepository, roleRepository, objectMapper, mockMvc);
        this.authenticationService = authenticationService;
        this.encoder = encoder;
    }

    @Override
    public void setup() {
        super.setup();
        getRoleRepository().save(RoleEntity.builder().name(roleManager).build());
        getRoleRepository().save(RoleEntity.builder().name(roleUser).build());
    }

    @BeforeEach
    void standard(){
        getUserRepository().deleteAll();
        saveUser(usernameAdmin, "ADMIN");
    }

    private final void saveUser(String username, String enumRole) {
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

    private final String getBearerToken(String username){
        return "Bearer " + authenticationService.authenticate(username, password).getToken();
    }

    private final Set<String> getAllRoles() {
        return getRoleRepository().findAll().stream().map(RoleEntity::getName).collect(Collectors.toSet());
    }

    @Test
    void updateUserRoles() throws Exception{
        saveUser(usernameManager, roleManager);
        UpdateUserRolesForm updateUserRolesForm = new UpdateUserRolesForm()
            .username(usernameManager)
            .roles(getAllRoles());

        getMockMvc().perform(
            patch(url)
                .header(authorizationHeader, getBearerToken(usernameAdmin))
                .contentType(MediaType.APPLICATION_JSON)
                .content(getObjectMapper().writeValueAsString(updateUserRolesForm)))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.message").value(okAlterUserRole));
    }

    @Test
    void updateUserRoles_error_unauthorized() throws Exception{
        getMockMvc().perform(
            patch(url))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.message").value(errorNotAuthorized));
    }

    @Test
    void updateUserRoles_error_forbidden() throws Exception{
        saveUser(usernameManager, roleManager);
        UpdateUserRolesForm updateUserRolesForm = new UpdateUserRolesForm()
            .username(usernameManager)
            .roles(getAllRoles());
        
        getMockMvc().perform(
            patch(url)
                .header(authorizationHeader, getBearerToken(usernameManager))
                .contentType(MediaType.APPLICATION_JSON)
                .content(getObjectMapper().writeValueAsString(updateUserRolesForm)))
            .andExpect(status().isForbidden());
        
        saveUser(usernameUser, roleUser);
        updateUserRolesForm.username(usernameUser);

        getMockMvc().perform(
            patch(url)
                .header(authorizationHeader, getBearerToken(usernameUser))
                .contentType(MediaType.APPLICATION_JSON)
                .content(getObjectMapper().writeValueAsString(updateUserRolesForm)))
            .andExpect(status().isForbidden());
    }

    @Test
    void updateUserRoles_error_usernameNotFound() throws Exception{
        UpdateUserRolesForm updateUserRolesForm = new UpdateUserRolesForm()
            .username(usernameUser)
            .roles(getAllRoles());
        
        getMockMvc().perform(
            patch(url)
                .header(authorizationHeader, getBearerToken(usernameAdmin))
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
        saveUser(usernameUser, roleUser);
        saveUser(usernameManager, roleManager);
        
        getMockMvc().perform(
            get(url)
                .header(authorizationHeader, getBearerToken(usernameAdmin)))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$", hasSize(3)))
            .andExpect(jsonPath("$.[0].username", Matchers.containsString("@email.com")))
            .andExpect(jsonPath("$.[0].roles", hasSize(1)));
    }

    @Test
    void getAllUsers_branch_username() throws Exception{
        saveUser(usernameUser, roleUser);
        saveUser(usernameManager, roleManager);

        getMockMvc().perform(
            get(url)
                .header(authorizationHeader, getBearerToken(usernameAdmin))
                .queryParam("username", "manag"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$.[0].username").value(usernameManager))
            .andExpect(jsonPath("$.[0].roles[0]").value(roleManager));
    }

    @Test
    void getAllUsers_branch_role() throws Exception{
        saveUser(usernameUser, roleUser);
        saveUser(usernameManager, roleManager);

        getMockMvc().perform(
            get(url)
                .header(authorizationHeader, getBearerToken(usernameAdmin))
                .queryParam("role", roleUser))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$.[0].username").value(usernameUser))
            .andExpect(jsonPath("$.[0].roles[0]").value(roleUser));
    }

    @Test
    void getAllUsers_branch_usernameAndRole() throws Exception{
        saveUser(usernameUser, roleUser);
        saveUser(usernameManager, roleManager);

        getMockMvc().perform(
            get(url)
                .header(authorizationHeader, getBearerToken(usernameAdmin))
                .queryParam("username", "admin")
                .queryParam("role", roleUser))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$").isEmpty());
        
        getMockMvc().perform(
            get(url)
                .header(authorizationHeader, getBearerToken(usernameAdmin))
                .queryParam("username", "er@")
                .queryParam("role", roleUser))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$.[0].username").value(usernameUser))
            .andExpect(jsonPath("$.[0].roles[0]").value(roleUser));
    }

    @Test
    void getAllUsers_error_unauthorized() throws Exception{
        getMockMvc().perform(
            get(url))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.message").value(errorNotAuthorized));
    }

    @Test
    void getAllUsers_error_forbidden() throws Exception{
        saveUser(usernameManager, roleManager);
        getMockMvc().perform(
            get(url)
                .header(authorizationHeader, getBearerToken(usernameManager)))
            .andExpect(status().isForbidden());

        saveUser(usernameUser, roleUser);
        getMockMvc().perform(
            get(url)
                .header(authorizationHeader, getBearerToken(usernameUser)))
            .andExpect(status().isForbidden());
    }
    
}
