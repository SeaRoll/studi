package com.yohan.studi.integration;

import com.icegreen.greenmail.configuration.GreenMailConfiguration;
import com.icegreen.greenmail.junit5.GreenMailExtension;
import com.icegreen.greenmail.util.GreenMailUtil;
import com.icegreen.greenmail.util.ServerSetupTest;
import com.yohan.studi.email.Email;
import com.yohan.studi.email.EmailService;
import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.test.context.ActiveProfiles;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static io.zonky.test.db.AutoConfigureEmbeddedDatabase.RefreshMode.AFTER_EACH_TEST_METHOD;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureEmbeddedDatabase(refresh = AFTER_EACH_TEST_METHOD)
public class IT_EmailService {

    @RegisterExtension
    static GreenMailExtension greenMail = new GreenMailExtension(ServerSetupTest.SMTP)
            .withConfiguration(GreenMailConfiguration.aConfig().withUser("duke", "springboot"))
            .withPerMethodLifecycle(false);

    @Autowired
    private EmailService emailService;

    @Autowired
    private ThreadPoolTaskExecutor taskExecutor;

    @Test
    public void sendingSimpleEmailWorks() throws MessagingException, InterruptedException {
        // send an e-mail
        emailService.sendSimpleMessage("me@gmail.com", "tohan@gmail.com", "Hello", "World");
        taskExecutor.getThreadPoolExecutor().awaitTermination(1, TimeUnit.SECONDS);

        MimeMessage receivedMessage = greenMail.getReceivedMessages()[0];

        assertEquals("World", GreenMailUtil.getBody(receivedMessage));
        assertEquals(1, receivedMessage.getAllRecipients().length);
        assertEquals("tohan@gmail.com", receivedMessage.getAllRecipients()[0].toString());
    }

    @Test
    public void sendingTestEmailWorks() throws MessagingException, InterruptedException {
        Email email = new Email();
        email.setTo("tohan@gmail.com");
        email.setSubject("Hello");
        email.setTemplate("test-email.html");
        Map<String, Object> properties = new HashMap<>();
        properties.put("name", "Ashish");
        email.setProperties(properties);

        emailService.sendHtmlMessage(email);
        taskExecutor.getThreadPoolExecutor().awaitTermination(1, TimeUnit.SECONDS);

        MimeMessage receivedMessage = greenMail.getReceivedMessages()[0];
        assertEquals(1, receivedMessage.getAllRecipients().length);
        assertNotEquals("", receivedMessage.getAllRecipients()[0].toString());
    }
}
