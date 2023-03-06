package br.com.juliocauan.authentication.infrastructure.model;

import org.openapitools.model.EnumRole;

import br.com.juliocauan.authentication.domain.model.Role;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@Getter @EqualsAndHashCode
@AllArgsConstructor @NoArgsConstructor
@Builder
public final class RoleEntity implements Role {
    
	@Id @EqualsAndHashCode.Exclude
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Short id;
    
    @Enumerated(EnumType.STRING)
	@Column(length = 20, nullable = false)
    private EnumRole name;

}
