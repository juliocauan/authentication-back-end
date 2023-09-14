package br.com.juliocauan.authentication.infrastructure.model;

import java.time.LocalDateTime;

import br.com.juliocauan.authentication.domain.model.RecoveryToken;
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

@Entity @Table(name = "recovery_tokens", schema = "auth")
@Data @EqualsAndHashCode
@AllArgsConstructor @NoArgsConstructor
@Builder
public final class RecoveryTokenEntity implements RecoveryToken {
    
    @Id @EqualsAndHashCode.Exclude
	@GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank @Size(min = 43, max = 43)
    private String token;

    @OneToOne(targetEntity = UserEntity.class, fetch = FetchType.EAGER, cascade = CascadeType.REMOVE)
    @JoinColumn(nullable = false, unique = true, name = "user_id")
    private UserEntity user;

    @NotNull
    private LocalDateTime expireDate;

}
