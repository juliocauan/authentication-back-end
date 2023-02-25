package br.com.juliocauan.authentication.domain.model;

import java.util.Set;

public interface User {
    Long getId();
    String getUsername();
    String getPassword();
    Set<? extends Role> getRoles();
}
