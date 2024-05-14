package br.com.juliocauan.authentication.application.service;

import java.util.Collections;
import java.util.stream.Collectors;

import org.openapitools.model.UserData;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import br.com.juliocauan.authentication.domain.model.PasswordReset;
import br.com.juliocauan.authentication.domain.model.User;
import br.com.juliocauan.authentication.infrastructure.repository.PasswordResetRepository;
import br.com.juliocauan.authentication.infrastructure.repository.RoleRepository;
import br.com.juliocauan.authentication.infrastructure.repository.UserRepository;
import br.com.juliocauan.authentication.infrastructure.security.jwt.JwtProvider;
import br.com.juliocauan.authentication.util.EmailUtil;
import br.com.juliocauan.authentication.util.PasswordUtil;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtProvider;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordResetRepository passwordResetRepository;

    public UserData authenticate(String username, String password) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, password);
        Authentication auth = authenticationManager.authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(auth);
        
        return new UserData()
            .roles(auth.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toSet()))
            .JWT(jwtProvider.generateToken(auth));
    }

    public void registerUser(String username, String password) {
        userRepository.register(new User(username, password));
    }

    public void registerAdmin(String username, String password, String adminKey) {
        PasswordUtil.validateAdminKey(adminKey);
        User admin = new User(username, password);
        admin.setRoles(Collections.singleton(roleRepository.findByName("ADMIN")));
        userRepository.register(admin);
    }

    public void sendToken(String username) {
        User user = userRepository.findByUsername(username);
        String token = passwordResetRepository.register(user).getToken();
        EmailUtil.sendEmail(
                username,
                "Reset your password!",
                getEmailTemplate(token));
    }

    //TODO refactor
    private String getEmailTemplate(String token) {
        return "To reset your password, use the following token: %s %n%n This token will last %d minutes".formatted(
                token, PasswordReset.TOKEN_EXPIRATION_MINUTES);
    }

    public void resetPassword(String newPassword, String token) {
        PasswordReset passwordReset = passwordResetRepository.findByToken(token);
        userRepository.updatePassword(passwordReset.getUser(), newPassword);
        passwordResetRepository.delete(passwordReset);
    }
}
