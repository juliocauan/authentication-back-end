package br.com.juliocauan.authentication.util.emailers;

import com.icegreen.greenmail.configuration.GreenMailConfiguration;
import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.GreenMailUtil;
import com.icegreen.greenmail.util.ServerSetupTest;

public final class GreenMailEmailer implements Emailer {

    private final GreenMail greenMail = new GreenMail(ServerSetupTest.SMTP);

    @Override
    public void sendEmail(String receiver, String subject, String message) {
        GreenMailUtil.sendTextEmail(receiver, "GreenMail", subject, message, greenMail.getSmtp().getServerSetup());
    }

    @Override
    public void configure(String username, String key) {
        greenMail.withConfiguration(GreenMailConfiguration.aConfig().withUser(username, key));
        if (!greenMail.isRunning())
            greenMail.start();
    }

}
