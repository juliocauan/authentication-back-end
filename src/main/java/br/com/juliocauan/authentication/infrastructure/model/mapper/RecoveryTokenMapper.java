package br.com.juliocauan.authentication.infrastructure.model.mapper;

import br.com.juliocauan.authentication.domain.model.RecoveryToken;
import br.com.juliocauan.authentication.infrastructure.model.RecoveryTokenEntity;

public interface RecoveryTokenMapper {
    
    static RecoveryTokenEntity domainToEntity(RecoveryToken model) {
        return RecoveryTokenEntity.builder()
            .id(model.getId())
            .user(UserMapper.domainToEntity(model.getUser()))
            .token(model.getToken())
            .expireDate(model.getExpireDate())
        .build();
    }

}
