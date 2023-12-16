package br.com.juliocauan.authentication.domain.model;

import java.util.Set;

public abstract class User {
    public abstract Integer getId();
    public abstract String getUsername();
    public abstract String getPassword();
    public abstract Set<? extends Role> getRoles();

    public static final User changePassword(final User user, String newPassword) {
        return newUser(user.getId(), user.getUsername(), newPassword, user.getRoles());
    }

    public static final User changeRoles(final User user, Set<? extends Role> newRoles) {
        return newUser(user.getId(), user.getUsername(), user.getPassword(), newRoles);
    }

    private static final User newUser(Integer id, String username, String password, Set<? extends Role> roles) {
        return new User() {
            @Override
            public Integer getId() { return id; }
            @Override
            public String getUsername() { return username; }
            @Override
            public String getPassword() { return password; }
            @Override
            public Set<? extends Role> getRoles() { return roles; } 
        };
    }
}
