package br.com.juliocauan.authentication.domain.model;

import java.time.LocalDateTime;

public interface ResetPasswordToken {
    Long getId();
    User getUser();
    String getToken();
    LocalDateTime getExpireDate();
}
