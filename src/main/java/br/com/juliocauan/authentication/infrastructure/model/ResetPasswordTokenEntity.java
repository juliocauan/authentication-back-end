package br.com.juliocauan.authentication.infrastructure.model;

import java.time.LocalDateTime;

import br.com.juliocauan.authentication.domain.model.ResetPasswordToken;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity @Table(name = "password_reset_tokens", schema = "auth")
@Data @EqualsAndHashCode
@AllArgsConstructor @NoArgsConstructor
@Builder
public final class ResetPasswordTokenEntity implements ResetPasswordToken {
    
    @Id @EqualsAndHashCode.Exclude
	@GeneratedValue
    private Long id;

    @NotBlank @Size(min = 43, max = 43)
    private String token;

    @OneToOne(targetEntity = UserEntity.class, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, unique = true, name = "user_id")
    private UserEntity user;

    @NotBlank
    private LocalDateTime expireDate;

}
