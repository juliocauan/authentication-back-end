package br.com.juliomariano.authentication.application.service;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.juliomariano.authentication.domain.model.User;
import br.com.juliomariano.authentication.domain.service.UserService;
import br.com.juliomariano.authentication.util.PasswordUtil;
import lombok.AllArgsConstructor;

@Service
@Transactional
@AllArgsConstructor
public class ProfileService {

    private final UserService userService;

    public void updatePassword(String currentPassword, String newPassword) {
        User user = getLoggedUser();
        validateCurrentPassword(currentPassword, user);
        userService.updatePassword(user, newPassword);
    }

    public void closeAccount(String currentPassword) {
        User user = getLoggedUser();
        validateCurrentPassword(currentPassword, user);
        userService.delete(user);
    }

    private User getLoggedUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userService.findByUsername(username);
    }

    private void validateCurrentPassword(String rawCurrentPassword, User loggedUser) {
        PasswordUtil.validateMatch(rawCurrentPassword, loggedUser.getPassword());
    }

}
