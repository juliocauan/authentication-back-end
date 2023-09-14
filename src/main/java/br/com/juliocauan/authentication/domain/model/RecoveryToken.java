package br.com.juliocauan.authentication.domain.model;

import java.time.LocalDateTime;

public interface RecoveryToken {
    static final int EXPIRE = 30;
    Long getId();
    User getUser();
    String getToken();
    LocalDateTime getExpireDate();
    default boolean isExpired() {
        return LocalDateTime.now().isAfter(getExpireDate());
    }
}
