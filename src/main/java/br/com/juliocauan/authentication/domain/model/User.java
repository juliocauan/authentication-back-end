package br.com.juliocauan.authentication.domain.model;

import java.util.HashSet;
import java.util.Set;

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
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity @Table(name = "users", schema = "auth")
@Data @EqualsAndHashCode
@NoArgsConstructor
public final class User {
    
	@Id @EqualsAndHashCode.Exclude
	@GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

	@Email
	@Column(length = 60, unique = true, nullable = false)
	private String username;

	@NotBlank
	private String password;

    @ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "users_roles", schema = "auth",
        joinColumns = @JoinColumn(referencedColumnName = "id", name = "user_id"),
        inverseJoinColumns = @JoinColumn(referencedColumnName = "id", name = "role_id"))
	@EqualsAndHashCode.Exclude
	private Set<Role> roles = new HashSet<>();

    public User(String username, String password) {
        this();
        this.username = username;
        this.password = password;
    }

    // TODO remover
    public User(String username, String password, Set<Role> roles) {
        this(username, password);
        this.roles = roles;
    }
    
}
