package br.com.juliocauan.authentication.infrastructure.repository.specification;

import org.openapitools.model.EnumRole;
import org.springframework.data.jpa.domain.Specification;

import br.com.juliocauan.authentication.infrastructure.model.RoleEntity;
import br.com.juliocauan.authentication.infrastructure.model.UserEntity;
import jakarta.persistence.criteria.Join;

public interface UserSpecification {

    static Specification<UserEntity> usernameContains(String username){
        return (root, criteriaQuery, criteriaBuilder) -> {
            if(username == null) return null;
            return criteriaBuilder.like(root.get("username"), "%" + username + "%");
        };
    }

    static Specification<UserEntity> role(EnumRole role){
        return (root, criteriaQuery, criteriaBuilder) -> {
            if(role == null) return null;
            Join<RoleEntity, UserEntity> roles = root.join("roles");
            return criteriaBuilder.equal(roles.get("name"), role);
        };
    }
    
}
