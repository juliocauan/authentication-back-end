package br.com.juliocauan.authentication.infrastructure.model;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;

import br.com.juliocauan.authentication.domain.model.PasswordReset;
import br.com.juliocauan.authentication.domain.model.User;
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
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity @Table(name = "password_reset", schema = "auth")
@Data @EqualsAndHashCode(callSuper = false)
@AllArgsConstructor @NoArgsConstructor
@Builder
public final class PasswordResetEntity extends PasswordReset {
    
    @Id @EqualsAndHashCode.Exclude
	@GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank @Size(min = 43, max = 43)
    private String token;

    @OneToOne(targetEntity = User.class, fetch = FetchType.EAGER, cascade = CascadeType.DETACH)
    @JoinColumn(nullable = false, unique = true, name = "user_id")
    private User user;

    @NotNull
    @Builder.Default @EqualsAndHashCode.Exclude
    private LocalDateTime expireDate = LocalDateTime.now().plusMinutes(TOKEN_EXPIRATION_MINUTES);

    public PasswordResetEntity(User user) {
        this();
        this.token = generateToken();
        this.user = user;
    }

    private final String generateToken() {
        SecureRandom secureRandom = new SecureRandom();
        byte[] tokenBytes = new byte[TOKEN_LENGTH];
        secureRandom.nextBytes(tokenBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(tokenBytes);
    }

}
