package com.yohan.studi.unit;

import com.yohan.studi.exception.BadRequestException;
import com.yohan.studi.util.FormValidator;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

public class Unit_FormValidatorTest {
    private FormValidator formValidator;

    @Data
    @AllArgsConstructor
    private static class TestForm {
        @Size(min = 2, max = 20, message = "E-mail must be between 2 and 20")
        private String email;

        // Minimum eight characters, at least one uppercase letter, one lowercase letter and one number
        @Pattern(
                regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d]{8,}$",
                message = "Password is not eight characters, at least one uppercase letter, one lowercase letter and one number"
        )
        private String password;
    }

    @BeforeEach
    public void beforeEach() {
        formValidator = new FormValidator();
    }

    @Test
    public void validatingFormWorks() {
        // given right values
        TestForm test = new TestForm("test@gmail.com", "Yohan123");

        // assert does not throw
        Assertions.assertDoesNotThrow(() -> formValidator.validateForm(test));
    }

    @Test
    public void nonValidPassword_Throws() {
        // given right values
        TestForm test = new TestForm("test@gmail.com", "yohan123");

        // assert does not throw
        Assertions.assertThrows(BadRequestException.class, () -> formValidator.validateForm(test));
    }

    @Test
    public void tooShortEmail_Throws() {
        // given short email
        TestForm test = new TestForm("m", "Yohan123");

        // assert does throw
        Assertions.assertThrows(BadRequestException.class, () -> formValidator.validateForm(test));
    }

    @Test
    public void tooLongEmail_Throws() {
        // given long email
        TestForm test = new TestForm("thisisaverylongemailthatshouldbeverylong", "Yohan123");

        // assert does throw
        Assertions.assertThrows(BadRequestException.class, () -> formValidator.validateForm(test));
    }
}
