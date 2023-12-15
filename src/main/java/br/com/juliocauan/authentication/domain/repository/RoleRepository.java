package br.com.juliocauan.authentication.domain.repository;

import java.util.List;
import java.util.Optional;

import br.com.juliocauan.authentication.domain.model.Role;

public interface RoleRepository {
    Optional<Role> getByName(String name);
    List<Role> getAll(String nameContains);
}
