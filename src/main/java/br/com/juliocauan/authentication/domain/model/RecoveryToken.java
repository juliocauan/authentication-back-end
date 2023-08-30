package br.com.juliocauan.authentication.domain.model;

import java.time.LocalDateTime;

public interface RecoveryToken {
    Long getId();
    User getUser();
    String getToken();
    LocalDateTime getExpireDate();
}
