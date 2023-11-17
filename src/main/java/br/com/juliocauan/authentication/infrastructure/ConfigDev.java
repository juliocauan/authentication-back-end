package br.com.juliocauan.authentication.infrastructure;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import com.icegreen.greenmail.configuration.GreenMailConfiguration;
import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.ServerSetupTest;

@Configuration
@Profile(value = "dev")
public class ConfigDev {
    public static final GreenMail greenMail = new GreenMail(ServerSetupTest.SMTP)
        .withConfiguration(GreenMailConfiguration.aConfig().withUser("user", "admin"));
    
    public ConfigDev() {
        greenMail.start();
    }
}
