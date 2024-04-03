package br.com.juliocauan.authentication.infrastructure.repository.specification;

import org.springframework.data.jpa.domain.Specification;

import br.com.juliocauan.authentication.domain.model.Role;
import br.com.juliocauan.authentication.domain.model.User;
import jakarta.persistence.criteria.Join;

public interface UserSpecification {

    static Specification<User> usernameContains(String username){
        return (root, criteriaQuery, criteriaBuilder) -> {
            if(username == null) return null;
            return criteriaBuilder.like(root.get("username"), "%" + username + "%");
        };
    }

    static Specification<User> role(String role){
        return (root, criteriaQuery, criteriaBuilder) -> {
            if(role == null) return null;
            Join<Role, User> roles = root.join("roles");
            return criteriaBuilder.equal(roles.get("name"), role);
        };
    }
    
}
