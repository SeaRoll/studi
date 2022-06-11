package com.yohan.studi.system;

import com.icegreen.greenmail.configuration.GreenMailConfiguration;
import com.icegreen.greenmail.junit5.GreenMailExtension;
import com.icegreen.greenmail.util.ServerSetupTest;
import com.yohan.studi.user.User;
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
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.test.context.ActiveProfiles;

import java.util.HashMap;
import java.util.Objects;

import static io.zonky.test.db.AutoConfigureEmbeddedDatabase.RefreshMode.AFTER_EACH_TEST_METHOD;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureEmbeddedDatabase(refresh = AFTER_EACH_TEST_METHOD)
public class Sys_UserController {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private UserRepository userRepository;

    @RegisterExtension
    static GreenMailExtension greenMail = new GreenMailExtension(ServerSetupTest.SMTP)
            .withConfiguration(GreenMailConfiguration.aConfig().withUser("duke", "springboot"))
            .withPerMethodLifecycle(false);

    @BeforeEach
    public void setup() {
        restTemplate.getRestTemplate().setRequestFactory(new HttpComponentsClientHttpRequestFactory());
    }

    private HttpEntity<String> buildEntity(String token, JSONObject form) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        if(!Objects.isNull(token))
            headers.add("Authorization", "Bearer " + token);

        HttpEntity<String> request;
        if(!Objects.isNull(form))
            request = new HttpEntity<>(form.toString(), headers);
        else
            request = new HttpEntity<>(headers);
        return request;
    }

    private String getLoginToken() throws JSONException {
        JSONObject form = new JSONObject();
        form.put("email", "test@gmail.com");
        form.put("password", "Yohan123");

        HttpEntity<String> request = buildEntity(null, form);
        HashMap<String, Object> result = restTemplate.postForObject("http://localhost:" + port + "/api/v1/auth/login", request, HashMap.class);
        return (String) result.get("token");
    }

    @Test
    public void registerUser_Works() throws JSONException {
        JSONObject form = new JSONObject();
        form.put("email", "test@gmail.com");
        form.put("name", "test");
        form.put("password", "Yohan123");

        HttpEntity<String> request = buildEntity(null, form);
        ResponseEntity<String> result = restTemplate.postForEntity("http://localhost:" + port + "/api/v1/auth/register", request, String.class);
        System.out.println(result.toString());

        JSONObject jsonResult = new JSONObject(result.getBody());

        Assertions.assertEquals(HttpStatus.OK, result.getStatusCode());
        Assertions.assertTrue((Boolean) jsonResult.get("success"));
    }

    @Test
    public void registerUserInvalidForm_CorrectStatus() throws JSONException {
        JSONObject form = new JSONObject();
        form.put("email", "test@gmail.com");
        form.put("name", "test");
        form.put("password", "y");

        HttpEntity<String> request = buildEntity(null, form);
        ResponseEntity<String> result = restTemplate.postForEntity("http://localhost:" + port + "/api/v1/auth/register", request, String.class);
        System.out.println(result.toString());

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
    }

    @Test
    public void loginUserValid_Works() throws JSONException {
        registerUser_Works();
        String token = getLoginToken();
        assertTrue(token.length() > 10);
    }

    @Test
    public void loginUserTooManyRequest_Throws() throws JSONException {
        // register
        registerUser_Works();

        // form
        JSONObject form = new JSONObject();
        form.put("email", "test@gmail.com");
        form.put("password", "Yohan123");

        // build http entity
        HttpEntity<String> request = buildEntity(null, form);

        // run 10 times
        for(int i = 0; i < 10; i++)
            restTemplate.postForEntity("http://localhost:" + port + "/api/v1/auth/login", request, String.class);
        ResponseEntity<String> result = restTemplate.postForEntity("http://localhost:" + port + "/api/v1/auth/login", request, String.class);
        assertEquals(HttpStatus.TOO_MANY_REQUESTS, result.getStatusCode());
    }

    @Test
    public void forgotPassword_Works() throws JSONException {
        registerUser_Works();

        JSONObject form = new JSONObject();
        form.put("email", "test@gmail.com");

        HttpEntity<String> request = buildEntity(null, form);
        ResponseEntity<String> result = restTemplate.postForEntity("http://localhost:" + port + "/api/v1/auth/forgot", request, String.class);
        System.out.println(result.toString());

        JSONObject jsonResult = new JSONObject(result.getBody());

        Assertions.assertEquals(HttpStatus.OK, result.getStatusCode());
        Assertions.assertTrue((Boolean) jsonResult.get("success"));
    }

    @Test
    public void forgotPasswordTooManyRequest_Throws() throws JSONException {
        // register
        registerUser_Works();

        // form
        JSONObject form = new JSONObject();
        form.put("email", "test@gmail.com");

        // build http entity
        HttpEntity<String> request = buildEntity(null, form);

        // run once
        restTemplate.postForEntity("http://localhost:" + port + "/api/v1/auth/forgot", request, String.class);

        // should get too many requests
        ResponseEntity<String> result = restTemplate.postForEntity("http://localhost:" + port + "/api/v1/auth/forgot", request, String.class);
        assertEquals(HttpStatus.TOO_MANY_REQUESTS, result.getStatusCode());
    }
    
    @Test
    public void confirmPasswordValid_Works() throws JSONException {
        // register
        forgotPassword_Works();

        User user = userRepository.getUserByEmail("test@gmail.com").orElseThrow();

        // form
        JSONObject form = new JSONObject();
        form.put("email", "test@gmail.com");
        form.put("code", user.getResetToken());
        form.put("newPassword", "Yohan1232");

        // build http entity
        HttpEntity<String> request = buildEntity(null, form);

        // run once
        ResponseEntity<String> result = restTemplate.exchange("http://localhost:" + port + "/api/v1/auth/confirmforgot", HttpMethod.PATCH, request, String.class);
        JSONObject jsonResult = new JSONObject(result.getBody());

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertTrue((Boolean) jsonResult.get("success"));
    }

    @Test
    public void confirmPasswordInvalidWithoutForgot_Throws() throws JSONException {
        // register
        registerUser_Works();

        // form
        JSONObject form = new JSONObject();
        form.put("email", "test@gmail.com");
        form.put("code", "123123123");
        form.put("newPassword", "Yohan1232");

        // build http entity
        HttpEntity<String> request = buildEntity(null, form);

        // run once
        ResponseEntity<String> result = restTemplate.exchange("http://localhost:" + port + "/api/v1/auth/confirmforgot", HttpMethod.PATCH, request, String.class);
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
    }
}
