package br.com.juliomariano.authentication.util.emailers;

import com.icegreen.greenmail.configuration.GreenMailConfiguration;
import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.GreenMailUtil;
import com.icegreen.greenmail.util.ServerSetupTest;

public final class GreenMailEmailer implements Emailer {

    private static final GreenMail greenMail = new GreenMail(ServerSetupTest.SMTP);

    public GreenMailEmailer(String username, String accessKey) {
        greenMail.withConfiguration(GreenMailConfiguration.aConfig().withUser(username, accessKey));
        if(!greenMail.isRunning())
            greenMail.start();
    }

    @Override
    public void sendSimpleEmail(String receiver, String subject, String message) {
        GreenMailUtil.sendTextEmail(receiver, "GreenMailTester", subject, message, greenMail.getSmtp().getServerSetup());
    }

}
