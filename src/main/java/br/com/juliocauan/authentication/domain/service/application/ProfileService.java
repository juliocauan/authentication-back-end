package br.com.juliocauan.authentication.domain.service.application;

import br.com.juliocauan.authentication.domain.model.User;
import br.com.juliocauan.authentication.domain.service.UserService;
import br.com.juliocauan.authentication.util.PasswordUtil;

public abstract class ProfileService {

    protected abstract String getLoggedUsername();
    protected abstract UserService getUserService();

    public final void updatePassword(String currentPassword, String newPassword) {
        User user = validate(currentPassword);
        getUserService().updatePassword(user.getUsername(), newPassword);
    }

    public final void closeAccount(String password) {
        User user = validate(password);
        getUserService().delete(user.getUsername());
    }

    private final User validate(String currentPassword) {
        String username = getLoggedUsername();
        User loggedUser = getUserService().getBy(username);
        PasswordUtil.validatePasswordMatch(currentPassword, loggedUser.getPassword());
        return loggedUser;
    }
}
