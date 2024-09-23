package br.com.juliomariano.authentication.application.service;

import java.util.Collections;

import org.openapitools.model.UserData;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.juliomariano.authentication.domain.model.PasswordReset;
import br.com.juliomariano.authentication.domain.model.User;
import br.com.juliomariano.authentication.domain.service.PasswordResetService;
import br.com.juliomariano.authentication.domain.service.RoleService;
import br.com.juliomariano.authentication.domain.service.UserService;
import br.com.juliomariano.authentication.infrastructure.exception.ExpiredResetTokenException;
import br.com.juliomariano.authentication.infrastructure.security.jwt.JwtProvider;
import br.com.juliomariano.authentication.util.EmailUtil;
import br.com.juliomariano.authentication.util.PasswordUtil;
import lombok.AllArgsConstructor;

@Service
@Transactional
@AllArgsConstructor
public class AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final RoleService roleService;
    private final PasswordResetService passwordResetService;

    public UserData authenticate(String username, String password) {
        UserData userData = JwtProvider.authenticate(username, password, authenticationManager);
        return userData;
    }

    public void registerUser(String username, String password) {
        userService.register(new User(username, password));
    }

    public void registerAdmin(String username, String password, String adminKey) {
        PasswordUtil.validateAdminKey(adminKey);
        User admin = new User(username, password);
        admin.setRoles(Collections.singleton(roleService.findByName("ADMIN")));
        userService.register(admin);
    }

    public void sendToken(String username) {
        User user = userService.findByUsername(username);
        String token = passwordResetService.register(user).getToken();
        EmailUtil.sendEmail(
                username,
                "Reset your password!",
                getEmailTemplate(token));
    }

    private String getEmailTemplate(String token) {
        return "To reset your password, use the following token: %s %n%n This token will last %d minutes".formatted(
                token, PasswordReset.TOKEN_EXPIRATION_MINUTES);
    }

    @Transactional(noRollbackFor = ExpiredResetTokenException.class)
    public void resetPassword(String newPassword, String token) {
        PasswordReset passwordReset = passwordResetService.findByToken(token);
        userService.updatePassword(passwordReset.getUser(), newPassword);
        passwordResetService.delete(passwordReset);
    }
}
