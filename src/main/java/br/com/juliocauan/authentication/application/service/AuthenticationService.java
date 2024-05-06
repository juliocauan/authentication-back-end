package br.com.juliocauan.authentication.application.service;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

import org.openapitools.model.UserData;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import br.com.juliocauan.authentication.domain.model.Role;
import br.com.juliocauan.authentication.domain.model.User;
import br.com.juliocauan.authentication.infrastructure.repository.RoleRepository;
import br.com.juliocauan.authentication.infrastructure.repository.UserRepository;
import br.com.juliocauan.authentication.infrastructure.security.jwt.JwtProvider;
import br.com.juliocauan.authentication.util.PasswordUtil;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public final class AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtProvider;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

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
        admin.setRoles(getRoleSetWithAdmin());
        userRepository.register(admin);
    }

    private Set<Role> getRoleSetWithAdmin() {
        return Collections.singleton(roleRepository.findByName("ADMIN"));
    }
}
