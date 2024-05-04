package br.com.juliocauan.authentication.domain.service.application;

import br.com.juliocauan.authentication.domain.model.User;
import br.com.juliocauan.authentication.infrastructure.repository.UserRepository;
import br.com.juliocauan.authentication.util.PasswordUtil;

public abstract class ProfileService {

    protected abstract UserRepository getUserRepository();

    protected abstract String getLoggedUsername();

    public final void updatePassword(String currentPassword, String newPassword) {
        User user = validate(currentPassword);
        getUserRepository().updatePassword(user, newPassword);
    }

    public final void closeAccount(String currentPassword) {
        User user = validate(currentPassword);
        getUserRepository().delete(user);
    }

    private final User validate(String currentPassword) {
        String username = getLoggedUsername();
        User loggedUser = getUserRepository().findByUsername(username);
        PasswordUtil.validateMatch(currentPassword, loggedUser.getPassword());
        return loggedUser;
    }
}
