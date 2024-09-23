package br.com.juliomariano.authentication.infrastructure.config;

import org.openapitools.model.EmailType;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import br.com.juliomariano.authentication.util.EmailUtil;

@Configuration
@Profile("test")
public class TestConfig {
    
    TestConfig() {
        EmailUtil.setEmailer("admin@authentication.test", "admin", EmailType.GREEN_MAIL);
    }

}
