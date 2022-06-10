package com.yohan.studi.integration;

import com.icegreen.greenmail.configuration.GreenMailConfiguration;
import com.icegreen.greenmail.junit5.GreenMailExtension;
import com.icegreen.greenmail.util.ServerSetupTest;
import com.yohan.studi.exception.BadRequestException;
import com.yohan.studi.exception.TooManyException;
import com.yohan.studi.user.User;
import com.yohan.studi.user.UserForms.*;
import com.yohan.studi.user.UserRepository;
import com.yohan.studi.user.UserService;
import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import java.util.Date;

import static io.zonky.test.db.AutoConfigureEmbeddedDatabase.RefreshMode.AFTER_EACH_TEST_METHOD;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureEmbeddedDatabase(refresh = AFTER_EACH_TEST_METHOD)
public class IT_UserService {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @RegisterExtension
    static GreenMailExtension greenMail = new GreenMailExtension(ServerSetupTest.SMTP)
            .withConfiguration(GreenMailConfiguration.aConfig().withUser("duke", "springboot"))
            .withPerMethodLifecycle(false);

    @Test
    public void registerUserReturnsTrue() {
        // given valid form
        RegisterForm form = new RegisterForm("test@gmail.com", "test", "Yohan123");

        // creating user
        userService.registerUser(form);

        // check user is created
        User user = userRepository.getUserByEmail("test@gmail.com").orElseThrow();
        Assertions.assertEquals("test@gmail.com", user.getEmail());
        Assertions.assertEquals("test", user.getName());
        Assertions.assertNotEquals("Yohan", user.getHashedPassword());
    }

    @Test
    public void registerUserOnExistingEmail_Throws() {
        // register one user
        userService.registerUser(new RegisterForm("test@gmail.com", "test", "Yohan123"));

        // expect exception
        Exception ex = Assertions.assertThrows(BadRequestException.class, () -> userService.registerUser(new RegisterForm("test@gmail.com", "test", "Yohan123")));

        // assert message is equal
        Assertions.assertEquals("User with e-mail already exists", ex.getMessage());
    }

    @Test
    public void loginUserOnTooManyRequests_Throws() {
        // register
        userService.registerUser(new RegisterForm("test@gmail.com", "test", "Yohan123"));

        // try to log in with email 10 times
        for(int i = 0; i < 10; i++) {
            try {
                userService.loginUser(new LoginForm("test@gmail.com", "yohan123"));
            }catch (BadRequestException ex) {
                // do nothing
            }
        }
        Exception ex = Assertions.assertThrows(TooManyException.class, () -> userService.loginUser(new LoginForm("test@gmail.com", "yohan123")));
        Assertions.assertEquals("Too many requests", ex.getMessage());
    }

    @Test
    public void loginUserOnNoUser_Throws() {
        // try to log in without existing user
        Exception ex = Assertions.assertThrows(BadRequestException.class, () -> userService.loginUser(new LoginForm("test@gmail.com", "yohan123")));
        Assertions.assertEquals("User with e-mail test@gmail.com does not exist", ex.getMessage());
    }

    @Test
    public void loginUserWrongPassword_Throws() {
        // register
        userService.registerUser(new RegisterForm("test@gmail.com", "test", "Yohan123"));

        // assert wrong password
        Exception ex = Assertions.assertThrows(BadRequestException.class, () -> userService.loginUser(new LoginForm("test@gmail.com", "yohan123")));
        Assertions.assertEquals("Password does not match", ex.getMessage());
    }

    @Test
    public void loginUserValid_Token() {
        // register
        userService.registerUser(new RegisterForm("test@gmail.com", "test", "Yohan123"));

        // assert login does not throw
        String token = Assertions.assertDoesNotThrow(() -> userService.loginUser(new LoginForm("test@gmail.com", "Yohan123")));

        // check token is not empty
        Assertions.assertNotEquals("", token);
    }

    @Test
    public void forgotPasswordOnTooManyRequests_Throws() {
        // register
        userService.registerUser(new RegisterForm("test@gmail.com", "test", "Yohan123"));

        // normal forgot
        userService.forgotPassword(new ForgotPasswordForm("test@gmail.com"));

        // throw exception
        Exception ex = Assertions.assertThrows(TooManyException.class, () -> userService.forgotPassword(new ForgotPasswordForm("test@gmail.com")));

        // assert
        Assertions.assertEquals("Too many requests", ex.getMessage());
    }

    @Test
    public void forgotPasswordOnNoUser_Throws() {
        // try to forget password with non-existing e-mail
        Exception ex = Assertions.assertThrows(BadRequestException.class, () -> userService.forgotPassword(new ForgotPasswordForm("test@gmail.com")));
        Assertions.assertEquals("User with e-mail test@gmail.com does not exist", ex.getMessage());
    }

    @Test
    public void forgotPasswordValid_GeneratesToken() {
        // register
        userService.registerUser(new RegisterForm("test@gmail.com", "test", "Yohan123"));

        // use forgot password
        userService.forgotPassword(new ForgotPasswordForm("test@gmail.com"));

        // get user
        User user = userRepository.getUserByEmail("test@gmail.com").orElseThrow();

        // assert token exists
        Assertions.assertNotEquals("", user.getResetToken());

        // assert expiration date exists
        Assertions.assertTrue(new Date().getTime() < user.getResetTokenExpirationDate().getTime());
    }

    @Test
    public void forgotConfirmPasswordNoCode_Throws() {
        // register
        userService.registerUser(new RegisterForm("test@gmail.com", "test", "Yohan123"));

        Exception ex = Assertions.assertThrows(BadRequestException.class, () -> userService.confirmForgotPassword(new ConfirmForgotForm(
                "asdasdasd",
                "test@gmail.com",
                "test"
        )));

        Assertions.assertEquals("There are no reset code", ex.getMessage());
    }

    @Test
    public void forgotConfirmPasswordOnNoUser_Throws() {
        // try to forget password with non-existing e-mail
        Exception ex = Assertions.assertThrows(BadRequestException.class, () -> userService.confirmForgotPassword(new ConfirmForgotForm(
                "asdasdasd",
                "test@gmail.com",
                "Yohan123"
        )));
        Assertions.assertEquals("User with e-mail test@gmail.com does not exist", ex.getMessage());
    }

    @Test
    public void forgotConfirmPasswordNoCodeMatch_Throws() {
        // register
        userService.registerUser(new RegisterForm("test@gmail.com", "test", "Yohan123"));

        // forgot password
        userService.forgotPassword(new ForgotPasswordForm("test@gmail.com"));

        Exception ex = Assertions.assertThrows(BadRequestException.class, () -> userService.confirmForgotPassword(new ConfirmForgotForm(
                "asdasdasd",
                "test@gmail.com",
                "Yohan123"
        )));

        Assertions.assertEquals("Code does not match", ex.getMessage());
    }

    @Test
    public void forgotConfirmPasswordValid_ChangesPassword() {
        // register
        userService.registerUser(new RegisterForm("test@gmail.com", "test", "Yohan123"));

        // forgot password
        userService.forgotPassword(new ForgotPasswordForm("test@gmail.com"));

        // get user
        User user = userRepository.getUserByEmail("test@gmail.com").orElseThrow();

        // check password
        userService.confirmForgotPassword(new ConfirmForgotForm(
                user.getResetToken(),
                "test@gmail.com",
                "Yohan1232"
        ));

        // get user again
        user = userRepository.getUserByEmail("test@gmail.com").orElseThrow();

        // assert password is changed
        Assertions.assertTrue(passwordEncoder.matches("Yohan1232", user.getHashedPassword()));

        // assert reset token is gone
        Assertions.assertEquals("", user.getResetToken());
    }
}
