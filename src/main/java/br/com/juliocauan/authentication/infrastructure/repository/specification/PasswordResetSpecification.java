package br.com.juliocauan.authentication.infrastructure.repository.specification;

import org.springframework.data.jpa.domain.Specification;

import br.com.juliocauan.authentication.domain.model.PasswordReset;
import br.com.juliocauan.authentication.domain.model.User;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;

public interface PasswordResetSpecification {

    static Specification<PasswordReset> tokenEquals(String token){
        return (root, criteriaQuery, criteriaBuilder) -> {
            return criteriaBuilder.like(root.get("token"), token == null ? "" : token);
        };
    }

    static Specification<PasswordReset> userEquals(User user){
        return (root, criteriaQuery, criteriaBuilder) -> {
            Join<User, PasswordReset> userJoin = root.join("user", JoinType.INNER);
            return criteriaBuilder.equal(userJoin.get("username"), user.getUsername());
        };
    }

}
