package br.com.juliocauan.authentication.infrastructure.repository.specification;

import org.springframework.data.jpa.domain.Specification;

import br.com.juliocauan.authentication.domain.model.Role;

public interface RoleSpecification {

    static Specification<Role> nameContains(String nameContains){
        return (root, criteriaQuery, criteriaBuilder) -> {
            if(nameContains == null) return null;
            return criteriaBuilder.like(root.get("name"), "%" + nameContains + "%");
        };
    }

}
