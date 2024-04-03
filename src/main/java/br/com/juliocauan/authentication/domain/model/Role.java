package br.com.juliocauan.authentication.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity @Table(name = "roles", schema = "auth")
@Getter @EqualsAndHashCode
@NoArgsConstructor
public final class Role {
    
	@Id @EqualsAndHashCode.Exclude
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Short id;
    
	@Column(length = 40, nullable = false, unique = true)
    private String name;

	public Role(String name) {
		this();
		this.name = name;
	}
}
    