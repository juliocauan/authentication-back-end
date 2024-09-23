package br.com.juliomariano.authentication.infrastructure.repository.specification;

import org.springframework.data.jpa.domain.Specification;

import br.com.juliomariano.authentication.domain.model.Role;
import br.com.juliomariano.authentication.domain.model.User;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;

public interface UserSpecification {

    static Specification<User> usernameContains(String username){
        return (root, criteriaQuery, criteriaBuilder) -> {
            if(username == null) return null;
            return criteriaBuilder.like(root.get("username"), "%" + username + "%");
        };
    }

    static Specification<User> usernameEquals(String usernameEquals){
        return (root, criteriaQuery, criteriaBuilder) -> {
            if(usernameEquals == null) return null;
            return criteriaBuilder.like(root.get("username"), usernameEquals);
        };
    }

    static Specification<User> hasRole(String role){
        return (root, criteriaQuery, criteriaBuilder) -> {
            if(role == null) return null;
            Join<Role, User> roles = root.join("roles", JoinType.INNER);
            return criteriaBuilder.equal(roles.get("name"), role);
        };
    }
    
}
