package br.com.juliocauan.authentication.infrastructure.model;

import br.com.juliocauan.authentication.domain.model.Role;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity @Table(name = "roles", schema = "auth")
@Getter @EqualsAndHashCode(callSuper = false)
@AllArgsConstructor @NoArgsConstructor
@Builder
public final class RoleEntity extends Role {
    
	@Id @EqualsAndHashCode.Exclude
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Short id;
    
	@Column(length = 40, nullable = false, unique = true)
    private String name;

	public RoleEntity(Role role) {
		this.id = role.getId();
		this.name = role.getName();
	}

}
