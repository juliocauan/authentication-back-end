package br.com.juliocauan.authentication.infrastructure.model.mapper;

import br.com.juliocauan.authentication.domain.model.ResetPasswordToken;
import br.com.juliocauan.authentication.infrastructure.model.ResetPasswordTokenEntity;

public interface ResetPasswordTokenMapper {
    
    static ResetPasswordTokenEntity domainToEntity(ResetPasswordToken model) {
        return ResetPasswordTokenEntity.builder()
            .id(model.getId())
            .user(UserMapper.domainToEntity(model.getUser()))
            .token(model.getToken())
            .expireDate(model.getExpireDate())
        .build();
    }

}
