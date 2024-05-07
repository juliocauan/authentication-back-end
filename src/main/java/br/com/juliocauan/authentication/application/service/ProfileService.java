package br.com.juliocauan.authentication.application.service;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import br.com.juliocauan.authentication.domain.model.User;
import br.com.juliocauan.authentication.infrastructure.repository.UserRepository;
import br.com.juliocauan.authentication.util.PasswordUtil;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public final class ProfileService {

    private final UserRepository userRepository;

    public void updatePassword(String currentPassword, String newPassword) {
        User user = getLoggedUser(currentPassword);
        validateCurrentPassword(currentPassword, user);
        userRepository.updatePassword(user, newPassword);
    }

    public void closeAccount(String currentPassword) {
        User user = getLoggedUser(currentPassword);
        validateCurrentPassword(currentPassword, user);
        userRepository.delete(user);
    }

    private User getLoggedUser(String rawCurrentPassword) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username);
    }

    private void validateCurrentPassword(String rawCurrentPassword, User loggedUser) {
        PasswordUtil.validateMatch(rawCurrentPassword, loggedUser.getPassword());
    }

}
