package br.com.juliocauan.authentication.domain.model;

import java.time.LocalDateTime;

public abstract class PasswordReset {

    protected static final int TOKEN_LENGTH = 32;
    public static final int TOKEN_EXPIRATION_MINUTES = 10;

    public abstract Integer getId();
    public abstract User getUser();
    public abstract String getToken();
    public abstract LocalDateTime getExpireDate();
    
    public final boolean isExpired() {
        return LocalDateTime.now().isAfter(getExpireDate());
    }
}
