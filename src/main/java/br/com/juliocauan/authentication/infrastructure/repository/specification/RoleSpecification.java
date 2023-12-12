package br.com.juliocauan.authentication.infrastructure.repository.specification;

import org.springframework.data.jpa.domain.Specification;

import br.com.juliocauan.authentication.infrastructure.model.RoleEntity;

public interface RoleSpecification {

    static Specification<RoleEntity> roleContains(String contains){
        return (root, criteriaQuery, criteriaBuilder) -> {
            if(contains == null) return null;
            return criteriaBuilder.like(root.get("name"), "%" + contains + "%");
        };
    }

}
