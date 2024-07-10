package br.com.juliocauan.authentication.application.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openapitools.model.DeleteRoleRequest;
import org.openapitools.model.DisableUserRequest;
import org.openapitools.model.EmailAccess;
import org.openapitools.model.EmailType;
import org.openapitools.model.RegisterRoleRequest;
import org.openapitools.model.UpdateUserRolesForm;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.juliocauan.authentication.application.service.AuthenticationService;
import br.com.juliocauan.authentication.config.TestContext;
import br.com.juliocauan.authentication.domain.model.Role;
import br.com.juliocauan.authentication.domain.model.User;
import br.com.juliocauan.authentication.infrastructure.repository.RoleRepository;
import br.com.juliocauan.authentication.infrastructure.repository.UserRepository;

class AdminControllerTest extends TestContext {

    private final AuthenticationService authenticationService;
    private final PasswordEncoder encoder;

    private final String urlAdminUsers = "/admin/users";
    private final String urlAdminRoles = "/admin/roles";
    private final String urlAdminEmail = "/admin/email";
    private final String authorizationHeader = "Authorization";

    private final String usernameAdmin = getRandomUsername();
    private final String rawPassword = getRandomPassword();
    private final String errorNotAuthorized = "Full authentication is required to access this resource";

    public AdminControllerTest(UserRepository userRepository, RoleRepository roleRepository,
            ObjectMapper objectMapper, MockMvc mockMvc, AuthenticationService authenticationService, PasswordEncoder encoder) {
        super(userRepository, roleRepository, objectMapper, mockMvc);
        this.authenticationService = authenticationService;
        this.encoder = encoder;
    }

    @BeforeEach
    void beforeEach(){
        getUserRepository().deleteAll();
        getRoleRepository().deleteAll();
        getRoleRepository().save(new Role("ADMIN"));
        saveUser(usernameAdmin, "ADMIN");
    }

    private final String saveRole() {
        return getRoleRepository().save(new Role(getRandomString(15))).getName();
    }

    private final void saveUser(String username, String roleName) {
        Set<Role> roles = Collections.singleton(getRoleRepository().findByName(roleName));
        User user = new User(username, encoder.encode(rawPassword));
        user.setRoles(roles);
        getUserRepository().save(user);
    }

    private final String getAdminToken(){
        return "Bearer " + authenticationService.authenticate(usernameAdmin, rawPassword).getJWT();
    }

    private final String getToken(String username) {
        return "Bearer " + authenticationService.authenticate(username, rawPassword).getJWT();
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
    void updateUserRoles_branch_nullRole() throws Exception{
        String username = getRandomUsername();
        saveUser(username, saveRole());
        UpdateUserRolesForm updateUserRolesForm = new UpdateUserRolesForm()
            .username(username)
            .roles(new HashSet<>());

        getMockMvc().perform(
            patch(urlAdminUsers)
                .header(authorizationHeader, getAdminToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(writeValueAsString(updateUserRolesForm)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message").value(getOkAlterUserRoles(username, new HashSet<>())));
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
            .andExpect(jsonPath("$.message").value("Username [%s] not found!".formatted(username)))
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
    void disableUser() throws Exception {
        String username = getRandomUsername();
        saveUser(username, saveRole());
        DisableUserRequest disableUserRequest = new DisableUserRequest().username(username);

        getMockMvc().perform(
            delete(urlAdminUsers)
                .header(authorizationHeader, getAdminToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(writeValueAsString(disableUserRequest)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message").value("User [%s] disabled successfully!".formatted(username)));
    }

    @Test
    void disableUser_error_invalidInput() throws Exception {
        DisableUserRequest disableUserRequest = new DisableUserRequest().username(getRandomString(5));

        getMockMvc().perform(
            delete(urlAdminUsers)
                .header(authorizationHeader, getAdminToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(writeValueAsString(disableUserRequest)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("Input validation error!"))
            .andExpect(jsonPath("$.fieldErrors", hasSize(1)));
    }

    @Test
    void disableUser_error_adminException() throws Exception {
        DisableUserRequest disableUserRequest = new DisableUserRequest().username(usernameAdmin);

        getMockMvc().perform(
            delete(urlAdminUsers)
                .header(authorizationHeader, getAdminToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(writeValueAsString(disableUserRequest)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("You can not update/delete your own account here!"));
    }

    @Test
    void disableUser_error_unauthorized() throws Exception {
        getMockMvc().perform(
            delete(urlAdminUsers))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.message").value(errorNotAuthorized));
    }

    @Test
    void disableUser_error_forbidden() throws Exception {
        String username = getRandomUsername();
        saveUser(username, saveRole());
        DisableUserRequest disableUserRequest = new DisableUserRequest().username(username);
        
        getMockMvc().perform(
            delete(urlAdminUsers)
                .header(authorizationHeader, getToken(username))
                .contentType(MediaType.APPLICATION_JSON)
                .content(writeValueAsString(disableUserRequest)))
            .andExpect(status().isForbidden());
    }

    @Test
    void getRoles() throws Exception {
        saveRole();
        saveRole();

        getMockMvc().perform(
            get(urlAdminRoles)
                .header(authorizationHeader, getAdminToken()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(3)));
    }

    @Test
    void getRoles_branch_nameContains() throws Exception {
        String role = saveRole();

        getMockMvc().perform(
            get(urlAdminRoles)
                .header(authorizationHeader, getAdminToken())
                .queryParam("nameContains", role))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void getRoles_branch_notPresentNameContains() throws Exception {
        getMockMvc().perform(
            get(urlAdminRoles)
                .header(authorizationHeader, getAdminToken())
                .queryParam("nameContains", getRandomString(15)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void getRoles_error_unauthenticated() throws Exception {
        getMockMvc().perform(
            get(urlAdminRoles))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.message").value(errorNotAuthorized));
    }

    @Test
    void getRoles_error_forbidden() throws Exception {
        String username = getRandomUsername();
        saveUser(username, saveRole());
        getMockMvc().perform(
            get(urlAdminRoles)
                .header(authorizationHeader, getToken(username)))
            .andExpect(status().isForbidden());
    }

    @Test
    void registerRole() throws Exception {
        String role = getRandomString(5);
        RegisterRoleRequest registerRoleRequest = new RegisterRoleRequest().role(role);
        getMockMvc().perform(
            post(urlAdminRoles)
                .header(authorizationHeader, getAdminToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(writeValueAsString(registerRoleRequest)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.message").value("Role [%s] registered successfully!".formatted(role)));
    }
    
    @Test
    void registerRole_error_invalidInput() throws Exception {
        RegisterRoleRequest registerRoleRequest = new RegisterRoleRequest().role(getRandomString(2));
        getMockMvc().perform(
            post(urlAdminRoles)
                .header(authorizationHeader, getAdminToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(writeValueAsString(registerRoleRequest)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("Input validation error!"))
            .andExpect(jsonPath("$.fieldErrors", hasSize(1)));
    }
    
    @Test
    void registerRole_error_roleExists() throws Exception {
        String role = saveRole();
        RegisterRoleRequest registerRoleRequest = new RegisterRoleRequest().role(role);
        getMockMvc().perform(
            post(urlAdminRoles)
                .header(authorizationHeader, getAdminToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(writeValueAsString(registerRoleRequest)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("Role [%s] already exists!".formatted(role)));
    }

    @Test
    void registerRole_error_unauthenticated() throws Exception {
        getMockMvc().perform(
            post(urlAdminRoles))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.message").value(errorNotAuthorized));
    }

    @Test
    void registerRole_error_forbidden() throws Exception {
        String username = getRandomUsername();
        String role = saveRole();
        RegisterRoleRequest registerRoleRequest = new RegisterRoleRequest().role(role);
        saveUser(username, role);
        getMockMvc().perform(
            post(urlAdminRoles)
                .header(authorizationHeader, getToken(username))
                .contentType(MediaType.APPLICATION_JSON)
                .content(writeValueAsString(registerRoleRequest)))
            .andExpect(status().isForbidden());
    }

    @Test
    void deleteRole() throws Exception {
        String role = saveRole();
        DeleteRoleRequest deleteRoleRequest = new DeleteRoleRequest().role(role);
        getMockMvc().perform(
            delete(urlAdminRoles)
                .header(authorizationHeader, getAdminToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(writeValueAsString(deleteRoleRequest)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message").value("Role [%s] deleted successfully!".formatted(role)));
    }

    @Test
    void deleteRole_error_invalidInput() throws Exception {
        DeleteRoleRequest deleteRoleRequest = new DeleteRoleRequest().role(getRandomString(2));
        getMockMvc().perform(
            delete(urlAdminRoles)
                .header(authorizationHeader, getAdminToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(writeValueAsString(deleteRoleRequest)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("Input validation error!"))
            .andExpect(jsonPath("$.fieldErrors", hasSize(1)));
    }

    @Test
    void deleteRole_error_roleNotFound() throws Exception {
        String role = getRandomString(15);
        DeleteRoleRequest deleteRoleRequest = new DeleteRoleRequest().role(role);
        getMockMvc().perform(
            delete(urlAdminRoles)
                .header(authorizationHeader, getAdminToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(writeValueAsString(deleteRoleRequest)))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.message").value("Role [%s] not found!".formatted(role)));
    }

    @Test
    void deleteRole_error_adminException() throws Exception {
        DeleteRoleRequest deleteRoleRequest = new DeleteRoleRequest().role("ADMIN");
        getMockMvc().perform(
            delete(urlAdminRoles)
                .header(authorizationHeader, getAdminToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(writeValueAsString(deleteRoleRequest)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("Role [ADMIN] can not be deleted!"));
    }

    @Test
    void deleteRole_error_unauthenticated() throws Exception {
        getMockMvc().perform(
            delete(urlAdminRoles))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.message").value(errorNotAuthorized));
    }

    @Test
    void deleteRole_error_forbidden() throws Exception {
        String username = getRandomUsername();
        String role = saveRole();
        DeleteRoleRequest deleteRoleRequest = new DeleteRoleRequest().role(role);
        saveUser(username, role);
        getMockMvc().perform(
            delete(urlAdminRoles)
                .header(authorizationHeader, getToken(username))
                .contentType(MediaType.APPLICATION_JSON)
                .content(writeValueAsString(deleteRoleRequest)))
            .andExpect(status().isForbidden());
    }

    @Test
    void setEmailer() throws Exception {
        EmailAccess emailAccess =  new EmailAccess().username(getRandomUsername()).key(getRandomPassword());
        String emailerType = EmailType.GREEN_MAIL.getValue();
        getMockMvc().perform(
            put(urlAdminEmail)
                .header(authorizationHeader, getAdminToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(writeValueAsString(emailAccess))
                .queryParam("emailerType", emailerType))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message").value("[%s] set successfully!".formatted(emailerType)));
    }

    @Test
    void setEmailer_error_unauthenticated() throws Exception {
        getMockMvc().perform(
            put(urlAdminEmail)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.message").value(errorNotAuthorized));   
    }

    @Test
    void setEmailer_error_forbidden() throws Exception {
        EmailAccess emailAccess =  new EmailAccess().username(getRandomUsername()).key(getRandomPassword());
        String emailerType = EmailType.GREEN_MAIL.getValue();
        String username = getRandomUsername();
        String role = saveRole();
        saveUser(username, role);
        getMockMvc().perform(
            put(urlAdminEmail)
                .header(authorizationHeader, getToken(username))
                .contentType(MediaType.APPLICATION_JSON)
                .content(writeValueAsString(emailAccess))
                .queryParam("emailerType", emailerType))
            .andExpect(status().isForbidden());        
    }
    
}
