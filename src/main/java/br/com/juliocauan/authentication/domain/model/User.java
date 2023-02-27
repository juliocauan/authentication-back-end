package br.com.juliocauan.authentication.domain.model;

import java.util.Set;
import java.util.UUID;

public interface User {
    UUID getId();
    String getUsername();
    String getPassword();
    Set<? extends Role> getRoles();
}
