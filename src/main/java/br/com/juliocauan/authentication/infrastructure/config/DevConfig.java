package br.com.juliocauan.authentication.infrastructure.config;

import org.openapitools.model.EmailType;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import br.com.juliocauan.authentication.util.EmailUtil;

@Configuration
@Profile("dev")
public class DevConfig {
    
    DevConfig() {
        EmailUtil.setEmailer("admin@authentication.dev", "admin", EmailType.GREEN_MAIL);
    }
    
}
