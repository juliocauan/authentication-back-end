package br.com.juliocauan.authentication.infrastructure.repository;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.openapitools.model.EnumRole;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import br.com.juliocauan.authentication.domain.model.User;
import br.com.juliocauan.authentication.domain.repository.UserRepository;
import br.com.juliocauan.authentication.infrastructure.model.UserEntity;
import br.com.juliocauan.authentication.infrastructure.model.mapper.UserMapper;
import br.com.juliocauan.authentication.infrastructure.repository.specification.UserSpecification;

public interface UserRepositoryImpl extends UserRepository, JpaRepository<UserEntity, UUID>, JpaSpecificationExecutor<UserEntity> {

    @Override
    default List<User> findAllByUsernameContainingAndRole(String username, EnumRole role) {
        List<UserEntity> users = findAll(Specification.where(
            UserSpecification.usernameContains(username)
            .and(UserSpecification.role(role))));
        return users.stream().map(UserMapper::entityToDomain).collect(Collectors.toList());
    }

}
