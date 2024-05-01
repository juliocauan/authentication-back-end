package br.com.juliocauan.authentication.infrastructure.repository.specification;

import org.springframework.data.jpa.domain.Specification;

import br.com.juliocauan.authentication.domain.model.PasswordReset;
import br.com.juliocauan.authentication.domain.model.User;
import jakarta.persistence.criteria.Join;

public interface PasswordResetSpecification {

    static Specification<PasswordReset> tokenEquals(String token){
        return (root, criteriaQuery, criteriaBuilder) -> {
            if(token == null) return null;
            return criteriaBuilder.like(root.get("token"), token);
        };
    }

    static Specification<PasswordReset> userEquals(User user){
        return (root, criteriaQuery, criteriaBuilder) -> {
            if(user == null) return null;
            Join<User, PasswordReset> userJoin = root.join("user");
            return criteriaBuilder.equal(userJoin.get("username"), user.getUsername());
        };
    }

}
