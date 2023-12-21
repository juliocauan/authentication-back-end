package br.com.juliocauan.authentication.domain.service.application;

import br.com.juliocauan.authentication.domain.model.User;
import br.com.juliocauan.authentication.domain.service.UserService;
import br.com.juliocauan.authentication.util.PasswordUtil;

public abstract class ProfileService {

    protected abstract UserService getUserService();

    protected abstract String getLoggedUsername();

    public final void updatePassword(String currentPassword, String newPassword) {
        User user = validate(currentPassword);
        getUserService().updatePassword(user, newPassword);
    }

    public final void closeAccount(String currentPassword) {
        User user = validate(currentPassword);
        getUserService().delete(user);
    }

    private final User validate(String currentPassword) {
        String username = getLoggedUsername();
        User loggedUser = getUserService().getByUsername(username);
        PasswordUtil.validateMatch(currentPassword, loggedUser.getPassword());
        return loggedUser;
    }
}
