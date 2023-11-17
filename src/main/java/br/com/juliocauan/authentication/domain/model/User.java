package br.com.juliocauan.authentication.domain.model;

import java.util.Set;

public abstract class User {
    public abstract Integer getId();
    public abstract String getUsername();
    public abstract String getPassword();
    public abstract Set<? extends Role> getRoles();
}
