package br.com.juliomariano.authentication.infrastructure.repository.specification;

import org.springframework.data.jpa.domain.Specification;

import br.com.juliomariano.authentication.domain.model.Role;

public interface RoleSpecification {

    static Specification<Role> nameContains(String nameContains){
        return (root, criteriaQuery, criteriaBuilder) -> {
            if(nameContains == null) return null;
            return criteriaBuilder.like(root.get("name"), "%" + nameContains + "%");
        };
    }

    static Specification<Role> nameEquals(String nameEquals){
        return (root, criteriaQuery, criteriaBuilder) -> {
            return criteriaBuilder.like(root.get("name"), nameEquals == null ? "" : nameEquals);
        };
    }

}
