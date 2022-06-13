package com.yohan.studi.system;

import com.icegreen.greenmail.configuration.GreenMailConfiguration;
import com.icegreen.greenmail.junit5.GreenMailExtension;
import com.icegreen.greenmail.util.ServerSetupTest;
import com.yohan.studi.user.UserForms.*;
import com.yohan.studi.user.UserService;
import org.json.JSONObject;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class Sys_Util {

    private final UserService userService;

    private final TestRestTemplate restTemplate;

    @Autowired
    public Sys_Util(UserService userService, TestRestTemplate restTemplate) {
        this.userService = userService;
        this.restTemplate = restTemplate;
        restTemplate.getRestTemplate().setRequestFactory(new HttpComponentsClientHttpRequestFactory());
    }

    public ResponseEntity<String> requestServer(int port, String path, HttpMethod method, HttpEntity<String> entity) {
        return restTemplate.exchange("http://localhost:" + port + path, method, entity, String.class);
    }

    public void registerUser() {
        userService.registerUser(new RegisterForm("test@gmail.com", "test", "Yohan123"));
    }

    public String loginUser() {
        return userService.loginUser(new LoginForm("test@gmail.com", "Yohan123"));
    }

    public HttpEntity<String> buildEntity(String token, JSONObject form) {
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
}
