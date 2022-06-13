package com.yohan.studi.system;

import com.icegreen.greenmail.configuration.GreenMailConfiguration;
import com.icegreen.greenmail.junit5.GreenMailExtension;
import com.icegreen.greenmail.util.ServerSetupTest;
import com.yohan.studi.user.UserRepository;
import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import static io.zonky.test.db.AutoConfigureEmbeddedDatabase.RefreshMode.AFTER_EACH_TEST_METHOD;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureEmbeddedDatabase(refresh = AFTER_EACH_TEST_METHOD)
public class Sys_SubjectController {

    @LocalServerPort
    private int port;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private Sys_Util sysUtil;

    private final String defaultRoute = "/api/v1/subjects/";

    @RegisterExtension
    static GreenMailExtension greenMail = new GreenMailExtension(ServerSetupTest.SMTP)
            .withConfiguration(GreenMailConfiguration.aConfig().withUser("duke", "springboot"))
            .withPerMethodLifecycle(false);

    @BeforeEach
    public void beforeEach() {
        sysUtil.registerUser();
    }

    @Test
    public void createSubject_Returns200() throws JSONException {
        JSONObject form = new JSONObject();
        form.put("name", "Hello World");

        HttpEntity<String> request = sysUtil.buildEntity(sysUtil.loginUser(), form);
        ResponseEntity<String> result = sysUtil.requestServer(port, defaultRoute, HttpMethod.POST, request);
        System.out.println(result.toString());

        JSONObject jsonResult = new JSONObject(result.getBody());
        Assertions.assertEquals(HttpStatus.OK, result.getStatusCode());
        Assertions.assertTrue((Boolean) jsonResult.get("success"));
    }

    @Test
    public void createSubjectEmpty_Returns400() {
        HttpEntity<String> request = sysUtil.buildEntity(sysUtil.loginUser(), null);
        ResponseEntity<String> result = sysUtil.requestServer(port, defaultRoute, HttpMethod.POST, request);
        System.out.println(result.toString());
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
    }

    @Test
    public void readSubjects_Returns200() throws JSONException {
        createSubject_Returns200();
        HttpEntity<String> request = sysUtil.buildEntity(sysUtil.loginUser(), null);
        ResponseEntity<String> result = sysUtil.requestServer(port, defaultRoute, HttpMethod.GET, request);
        System.out.println(result.toString());
        Assertions.assertEquals(HttpStatus.OK, result.getStatusCode());
    }

    @Test
    public void readSubjectsEmpty_Returns200() {
        HttpEntity<String> request = sysUtil.buildEntity(sysUtil.loginUser(), null);
        ResponseEntity<String> result = sysUtil.requestServer(port, defaultRoute, HttpMethod.GET, request);
        System.out.println(result.toString());
        Assertions.assertEquals(HttpStatus.OK, result.getStatusCode());
    }

    /*
    * patch subject returns 200
    * patch subject with empty form returns 400
    * delete subject returns 200
    * delete subject if non-existing returns 400
    * */
}
