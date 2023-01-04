package br.com.juliocauan.authentication.infrastructure.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.juliocauan.authentication.domain.repository.UserRepository;
import br.com.juliocauan.authentication.infrastructure.model.UserEntity;

public interface UserRepositoryImpl extends UserRepository, JpaRepository<UserEntity, Long> {

}
