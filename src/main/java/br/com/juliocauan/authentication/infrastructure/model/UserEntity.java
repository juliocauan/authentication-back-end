package br.com.juliocauan.authentication.infrastructure.model;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

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

@Entity @Table(name = "users")
@Data @EqualsAndHashCode(callSuper = false)
@AllArgsConstructor @NoArgsConstructor
@Builder
public final class UserEntity implements User, UserDetails {
	
	private static final long serialVersionUID = 1L;
    
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank @Size(min = 6, max = 20) @Column(unique = true)
	private String username;

	@Email
	@NotBlank @Size(max = 50) @Column(unique = true)
	private String email;

	@NotBlank @Size(min = 8, max = 120)
	private String password;

    @ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "users_roles",
        joinColumns = @JoinColumn(referencedColumnName = "id", name = "user_id"),
        inverseJoinColumns = @JoinColumn(referencedColumnName = "id", name = "role_id"))
	@Builder.Default
	private Set<RoleEntity> roles = new HashSet<>();

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return roles;
	}
	@Override
	public boolean isAccountNonExpired() {
		return true;
	}
	@Override
	public boolean isAccountNonLocked() {
		return true;
	}
	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}
	@Override
	public boolean isEnabled() {
		return true;
	}

}
