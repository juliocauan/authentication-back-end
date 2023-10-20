package br.com.juliocauan.authentication.domain.model;

import java.util.Set;
import java.util.UUID;

public abstract class User {
    public abstract UUID getId();
    public abstract String getUsername();
    public abstract String getPassword();
    public abstract Set<? extends Role> getRoles();
}
