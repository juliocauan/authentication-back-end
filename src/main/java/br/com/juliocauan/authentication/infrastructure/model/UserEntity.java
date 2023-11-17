package br.com.juliocauan.authentication.infrastructure.model;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import br.com.juliocauan.authentication.domain.model.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity @Table(name = "users", schema = "auth")
@Data @EqualsAndHashCode(callSuper = false)
@AllArgsConstructor @NoArgsConstructor
@Builder
public final class UserEntity extends User {
    
	@Id @EqualsAndHashCode.Exclude
	@GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

	@Email
	@NotBlank @Size(max = 50) @Column(unique = true)
	private String username;

	@NotBlank @Size(min = 8, max = 120)
	private String password;

    @ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "users_roles", schema = "auth",
        joinColumns = @JoinColumn(referencedColumnName = "id", name = "user_id"),
        inverseJoinColumns = @JoinColumn(referencedColumnName = "id", name = "role_id"))
	@Builder.Default @EqualsAndHashCode.Exclude
	private Set<RoleEntity> roles = new HashSet<>();

	public UserEntity(User user) {
		this.id = user.getId();
		this.username = user.getUsername();
		this.password = user.getPassword();
		this.roles = user.getRoles().stream().map(RoleEntity::new).collect(Collectors.toSet());
	}

}
