package br.com.juliocauan.authentication.infrastructure.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import com.icegreen.greenmail.configuration.GreenMailConfiguration;
import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.ServerSetupTest;

@Configuration
@Profile("dev")
public class DevConfig {

    private static final GreenMail greenMail = new GreenMail(ServerSetupTest.SMTP)
        .withConfiguration(GreenMailConfiguration.aConfig().withUser("admin@authentication.dev", "admin"));
    
    DevConfig() {
        if(!greenMail.isRunning())
            greenMail.start();
    }
}
