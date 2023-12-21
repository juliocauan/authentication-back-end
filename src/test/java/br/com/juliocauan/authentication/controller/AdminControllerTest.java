package br.com.juliocauan.authentication.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Collections;
import java.util.Set;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openapitools.model.DeleteUserRequest;
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

    private final String urlAdminUsers = "/admin/users";
    private final String authorizationHeader = "Authorization";

    private final String usernameAdmin = getRandomUsername();
    private final String rawPassword = getRandomPassword();
    private final String errorNotAuthorized = "Full authentication is required to access this resource";

    public AdminControllerTest(UserRepositoryImpl userRepository, RoleRepositoryImpl roleRepository,
            ObjectMapper objectMapper, MockMvc mockMvc, AuthenticationServiceImpl authenticationService, PasswordEncoder encoder) {
        super(userRepository, roleRepository, objectMapper, mockMvc);
        this.authenticationService = authenticationService;
        this.encoder = encoder;
    }

    @BeforeEach
    void beforeEach(){
        getUserRepository().deleteAll();
        getRoleRepository().deleteAll();
        getRoleRepository().save(RoleEntity.builder().name("ADMIN").build());
        saveUser(usernameAdmin, "ADMIN");
    }

    private final String saveRole() {
        return getRoleRepository().save(RoleEntity.builder().name(getRandomString(15)).build()).getName();
    }

    private final void saveUser(String username, String roleName) {
        Set<RoleEntity> roles = Collections.singleton(new RoleEntity(getRoleRepository().getByName(roleName).get()));
        getUserRepository().save(UserEntity
            .builder()
                .username(username)
                .password(encoder.encode(rawPassword))
                .roles(roles)
            .build());
    }

    private final String getAdminToken(){
        return "Bearer " + authenticationService.authenticate(usernameAdmin, rawPassword).getToken();
    }

    private final String getToken(String username) {
        return "Bearer " + authenticationService.authenticate(username, rawPassword).getToken();
    }

    private final String getOkAlterUserRoles(String username, Set<String> roles) {
        return "Patched [%s] successfully! Roles: %s".formatted(username, roles);
    }

    private final Set<String> roleSet(String role) {
        return Collections.singleton(role);
    }

    @Test
    void getUsers() throws Exception{
        String role = saveRole();
        saveUser(getRandomUsername(), role);
        saveUser(getRandomUsername(), role);
        
        getMockMvc().perform(
            get(urlAdminUsers)
                .header(authorizationHeader, getAdminToken()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(3)))
            .andExpect(jsonPath("$.[0].username", Matchers.containsString("@email.test")))
            .andExpect(jsonPath("$.[0].roles", hasSize(1)));
    }

    @Test
    void getUsers_branch_usernameContains() throws Exception{
        String role = saveRole();
        String usernameContains = getRandomUsername();
        saveUser(usernameContains, role);
        saveUser(getRandomUsername(), role);

        getMockMvc().perform(
            get(urlAdminUsers)
                .header(authorizationHeader, getAdminToken())
                .queryParam("usernameContains", usernameContains))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$.[0].username").value(usernameContains))
            .andExpect(jsonPath("$.[0].roles[0]").value(role));
    }

    @Test
    void getUsers_branch_role() throws Exception{
        String expectedRole = saveRole();
        String expectedUsername = getRandomUsername();
        saveUser(expectedUsername, expectedRole);
        saveUser(getRandomUsername(), saveRole());

        getMockMvc().perform(
            get(urlAdminUsers)
                .header(authorizationHeader, getAdminToken())
                .queryParam("role", expectedRole))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$.[0].username").value(expectedUsername))
            .andExpect(jsonPath("$.[0].roles[0]").value(expectedRole));
    }

    @Test
    void getUsers_branch_usernameAndRole() throws Exception{
        String expectedRole = saveRole();
        String expectedUsername = getRandomUsername();
        saveUser(expectedUsername, expectedRole);
        saveUser(getRandomUsername(), saveRole());

        getMockMvc().perform(
            get(urlAdminUsers)
                .header(authorizationHeader, getAdminToken())
                .queryParam("usernameContains", getRandomUsername())
                .queryParam("role", saveRole()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isEmpty());
        
        getMockMvc().perform(
            get(urlAdminUsers)
                .header(authorizationHeader, getAdminToken())
                .queryParam("usernameContains", expectedUsername)
                .queryParam("role", expectedRole))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$.[0].username").value(expectedUsername))
            .andExpect(jsonPath("$.[0].roles[0]").value(expectedRole));
    }

    @Test
    void getUsers_error_unauthorized() throws Exception{
        getMockMvc().perform(
            get(urlAdminUsers))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.message").value(errorNotAuthorized));
    }

    @Test
    void getUsers_error_forbidden() throws Exception{
        String username = getRandomUsername();
        saveUser(username, saveRole());
        getMockMvc().perform(
            get(urlAdminUsers)
                .header(authorizationHeader, getToken(username)))
            .andExpect(status().isForbidden());
    }

    @Test
    void updateUserRoles() throws Exception{
        String username = getRandomUsername();
        Set<String> newRoles = roleSet(saveRole());
        saveUser(username, saveRole());
        UpdateUserRolesForm updateUserRolesForm = new UpdateUserRolesForm()
            .username(username)
            .roles(newRoles);

        getMockMvc().perform(
            patch(urlAdminUsers)
                .header(authorizationHeader, getAdminToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(writeValueAsString(updateUserRolesForm)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message").value(getOkAlterUserRoles(username, newRoles)));
    }

    @Test
    void updateUserRoles_error_invalidInput() throws Exception {
        UpdateUserRolesForm updateUserRolesForm = new UpdateUserRolesForm()
            .username(getRandomString(5))
            .roles(null);

        getMockMvc().perform(
            patch(urlAdminUsers)
                .header(authorizationHeader, getAdminToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(writeValueAsString(updateUserRolesForm)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("Input validation error!"))
            .andExpect(jsonPath("$.fieldErrors", hasSize(2)));
    }

    @Test
    void updateUserRoles_error_adminException() throws Exception {
        UpdateUserRolesForm updateUserRolesForm = new UpdateUserRolesForm()
            .username(usernameAdmin)
            .roles(roleSet(saveRole()));

        getMockMvc().perform(
            patch(urlAdminUsers)
                .header(authorizationHeader, getAdminToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(writeValueAsString(updateUserRolesForm)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("You can not update/delete your own account here!"));
    }

    @Test
    void updateUserRoles_error_usernameNotFound() throws Exception{
        String username = getRandomUsername();
        UpdateUserRolesForm updateUserRolesForm = new UpdateUserRolesForm()
            .username(username)
            .roles(roleSet(saveRole()));
        
        getMockMvc().perform(
            patch(urlAdminUsers)
                .header(authorizationHeader, getAdminToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(writeValueAsString(updateUserRolesForm)))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.message").value(getErrorUsernameNotFound(username)))
            .andExpect(jsonPath("$.timestamp").isNotEmpty())
            .andExpect(jsonPath("$.fieldErrors").isEmpty());
    }

    @Test
    void updateUserRoles_error_roleNotFound() throws Exception{
        String username = getRandomUsername();
        String newRole = getRandomString(5);
        saveUser(username, saveRole());
        UpdateUserRolesForm updateUserRolesForm = new UpdateUserRolesForm()
            .username(username)
            .roles(roleSet(newRole));
        
        getMockMvc().perform(
            patch(urlAdminUsers)
                .header(authorizationHeader, getAdminToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(writeValueAsString(updateUserRolesForm)))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.message").value("Role [%s] not found!".formatted(newRole)))
            .andExpect(jsonPath("$.timestamp").isNotEmpty())
            .andExpect(jsonPath("$.fieldErrors").isEmpty());
    }

    @Test
    void updateUserRoles_error_unauthorized() throws Exception{
        getMockMvc().perform(
            patch(urlAdminUsers))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.message").value(errorNotAuthorized));
    }

    @Test
    void updateUserRoles_error_forbidden() throws Exception{
        String username = getRandomUsername();
        saveUser(username, saveRole());
        UpdateUserRolesForm updateUserRolesForm = new UpdateUserRolesForm()
            .username(username)
            .roles(roleSet(saveRole()));
        
        getMockMvc().perform(
            patch(urlAdminUsers)
                .header(authorizationHeader, getToken(username))
                .contentType(MediaType.APPLICATION_JSON)
                .content(writeValueAsString(updateUserRolesForm)))
            .andExpect(status().isForbidden());
    }

    @Test
    void deleteUser() throws Exception {
        String username = getRandomUsername();
        saveUser(username, saveRole());
        DeleteUserRequest deleteUserRequest = new DeleteUserRequest().username(username);

        getMockMvc().perform(
            delete(urlAdminUsers)
                .header(authorizationHeader, getAdminToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(writeValueAsString(deleteUserRequest)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message").value("User [%s] deleted successfully!".formatted(username)));
    }

    @Test
    void deleteUser_error_invalidInput() throws Exception {
        DeleteUserRequest deleteUserRequest = new DeleteUserRequest().username(getRandomString(5));

        getMockMvc().perform(
            delete(urlAdminUsers)
                .header(authorizationHeader, getAdminToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(writeValueAsString(deleteUserRequest)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("Input validation error!"))
            .andExpect(jsonPath("$.fieldErrors", hasSize(1)));
    }

    @Test
    void deleteUser_error_adminException() throws Exception {
        DeleteUserRequest deleteUserRequest = new DeleteUserRequest().username(usernameAdmin);

        getMockMvc().perform(
            delete(urlAdminUsers)
                .header(authorizationHeader, getAdminToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(writeValueAsString(deleteUserRequest)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("You can not update/delete your own account here!"));
    }

    @Test
    void deleteUser_error_usernameNotFound() throws Exception {
        String username = getRandomUsername();
        DeleteUserRequest deleteUserRequest = new DeleteUserRequest().username(username);

        getMockMvc().perform(
            delete(urlAdminUsers)
                .header(authorizationHeader, getAdminToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(writeValueAsString(deleteUserRequest)))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.message").value(getErrorUsernameNotFound(username)));
    }

    @Test
    void deleteUser_error_unauthorized() throws Exception {
        getMockMvc().perform(
            delete(urlAdminUsers))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.message").value(errorNotAuthorized));
    }

    @Test
    void deleteUser_error_forbidden() throws Exception {
        String username = getRandomUsername();
        saveUser(username, saveRole());
        DeleteUserRequest deleteUserRequest = new DeleteUserRequest().username(username);
        
        getMockMvc().perform(
            delete(urlAdminUsers)
                .header(authorizationHeader, getToken(username))
                .contentType(MediaType.APPLICATION_JSON)
                .content(writeValueAsString(deleteUserRequest)))
            .andExpect(status().isForbidden());
    }
    
}
