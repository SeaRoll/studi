package com.yohan.studi.unit;

import com.yohan.studi.exception.BadRequestException;
import com.yohan.studi.user.UserForms.*;
import com.yohan.studi.util.FormValidator;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class Unit_UserFormTest {

    private FormValidator formValidator;

    private final String[] invalidEmails = {
            "test",
            "test@",
            "gmail.com"
    };

    private final String[] invalidPasswords = {
            "123",
            "yohan123",
            "invalid",
            "aVeryLong123NameThatIsVeryLongAndShouldBeTooLong"
    };

    @BeforeAll
    public void before() {
        formValidator = new FormValidator();
    }

    @Test
    public void ForgotPasswordValid_Passes() {
        // given valid form
        ForgotPasswordForm form = new ForgotPasswordForm("test@gmail.com");

        // does not throw
        assertDoesNotThrow(() -> formValidator.validateForm(form));
    }

    @Test
    public void ForgotPasswordInvalid_Throws() {
        // given form
        ForgotPasswordForm form = new ForgotPasswordForm("test@gmail.com");

        // does throw for each invalid emails
        for(String invalidEmail : invalidEmails) {
            log.info("testing email {}", invalidEmail);
            ForgotPasswordForm invalidForm = new ForgotPasswordForm(form.getEmail());
            invalidForm.setEmail(invalidEmail);
            assertThrows(BadRequestException.class, () -> formValidator.validateForm(invalidForm));
        }
    }

    @Test
    public void ForgotConfirmPasswordValid_Passes() {
        // given form
        ConfirmForgotForm form = new ConfirmForgotForm("test123", "test@gmail.com", "Yohan123");

        // does not throw
        assertDoesNotThrow(() -> formValidator.validateForm(form));
    }

    @Test
    public void ForgotConfirmPasswordInvalid_Throws() {
        // given form
        ConfirmForgotForm form = new ConfirmForgotForm("test123", "test@gmail.com", "Yohan123");

        // does throw for each invalid emails
        for(String invalidEmail : invalidEmails) {
            log.info("testing email {}", invalidEmail);
            ConfirmForgotForm invalidForm = new ConfirmForgotForm(form.getCode(), form.getEmail(), form.getNewPassword());
            invalidForm.setEmail(invalidEmail);
            assertThrows(BadRequestException.class, () -> formValidator.validateForm(invalidForm));
        }

        // does throw for each invalid passwords
        for(String invalidPassword : invalidPasswords) {
            log.info("testing password {}", invalidPassword);
            ConfirmForgotForm invalidForm = new ConfirmForgotForm(form.getCode(), form.getEmail(), form.getNewPassword());
            invalidForm.setNewPassword(invalidPassword);
            assertThrows(BadRequestException.class, () -> formValidator.validateForm(invalidForm));
        }

        // throw on empty code
        ConfirmForgotForm invalidForm = new ConfirmForgotForm(form.getCode(), form.getEmail(), form.getNewPassword());
        invalidForm.setCode("");
        assertThrows(BadRequestException.class, () -> formValidator.validateForm(invalidForm));
    }

    @Test
    public void LoginFormValid_Passes() {
        // given valid form
        LoginForm form = new LoginForm("test@gmail.com", "Yohan123");

        // does not throw
        assertDoesNotThrow(() -> formValidator.validateForm(form));
    }

    @Test
    public void InvalidLoginFormValid_Throws() {
        // initiate form
        LoginForm form = new LoginForm("test@gmail.com", "Yohan123");

        // does throw for each invalid emails
        for(String invalidEmail : invalidEmails) {
            log.info("testing email {}", invalidEmail);
            LoginForm invalidForm = new LoginForm(form.getEmail(), form.getPassword());
            invalidForm.setEmail(invalidEmail);
            assertThrows(BadRequestException.class, () -> formValidator.validateForm(invalidForm));
        }

        LoginForm invalidForm = new LoginForm(form.getEmail(), form.getPassword());
        invalidForm.setPassword("");
        assertThrows(BadRequestException.class, () -> formValidator.validateForm(invalidForm));
    }

    @Test
    public void RegisterValidForm_Passes() {
        // given valid form
        RegisterForm form = new RegisterForm("test@gmail.com", "hello", "Yohan123");

        // does not throw
        assertDoesNotThrow(() -> formValidator.validateForm(form));
    }

    @Test
    public void RegisterInvalidForm_Fails() {
        String[] invalidNames = {
                "a",
                "aVeryLongNameThatIsVeryLongAndShouldBeTooLong"
        };
        // new form
        RegisterForm form = new RegisterForm("test@gmail.com", "hello", "Yohan123");

        // does throw for each invalid emails
        for(String invalidEmail : invalidEmails) {
            log.info("testing email {}", invalidEmail);
            RegisterForm invalidForm = new RegisterForm(form.getEmail(), form.getName(), form.getPassword());
            invalidForm.setEmail(invalidEmail);
            assertThrows(BadRequestException.class, () -> formValidator.validateForm(invalidForm));
        }

        // does throw for each invalid names
        for(String invalidName : invalidNames) {
            log.info("testing name {}", invalidName);
            RegisterForm invalidForm = new RegisterForm(form.getEmail(), form.getName(), form.getPassword());
            invalidForm.setName(invalidName);
            assertThrows(BadRequestException.class, () -> formValidator.validateForm(invalidForm));
        }

        // does throw for each invalid password
        for(String invalidPassword : invalidPasswords) {
            log.info("testing password {}", invalidPassword);
            RegisterForm invalidForm = new RegisterForm(form.getEmail(), form.getName(), form.getPassword());
            invalidForm.setPassword(invalidPassword);
            assertThrows(BadRequestException.class, () -> formValidator.validateForm(invalidForm));
        }
    }
}
