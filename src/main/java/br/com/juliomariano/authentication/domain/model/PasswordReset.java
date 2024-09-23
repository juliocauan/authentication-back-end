package br.com.juliomariano.authentication.domain.model;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity @Table(name = "password_reset", schema = "auth")
@Getter @EqualsAndHashCode
@NoArgsConstructor
public final class PasswordReset {

    private static final int TOKEN_LENGTH = 32;
    public static final int TOKEN_EXPIRATION_MINUTES = 10;

    @Id @EqualsAndHashCode.Exclude
	@GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank
    private String token;

    @OneToOne(targetEntity = User.class, fetch = FetchType.EAGER, cascade = CascadeType.DETACH)
    @JoinColumn(nullable = false, unique = true, name = "user_id")
    private User user;

    @NotNull @EqualsAndHashCode.Exclude
    @Setter
    private LocalDateTime expireDate = LocalDateTime.now().plusMinutes(TOKEN_EXPIRATION_MINUTES);

    public PasswordReset(User user) {
        this();
        this.token = generateToken();
        this.user = user;
    }

    public void update() {
        this.expireDate = LocalDateTime.now().plusMinutes(TOKEN_EXPIRATION_MINUTES);
        this.token = generateToken();
    }

    private String generateToken() {
        SecureRandom secureRandom = new SecureRandom();
        byte[] rawToken = new byte[TOKEN_LENGTH];
        secureRandom.nextBytes(rawToken);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(rawToken);
    }
    
    public final boolean isExpired() {
        return LocalDateTime.now().isAfter(this.getExpireDate());
    }
}
