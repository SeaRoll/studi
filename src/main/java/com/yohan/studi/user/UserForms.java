package com.yohan.studi.user;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.*;

public class UserForms {

    @Data
    @AllArgsConstructor
    public static class RegisterForm {

        @NotBlank(message = "E-mail cannot be empty")
        @Email(message = "Invalid e-mail")
        private String email;

        @NotBlank(message = "Name cannot be null")
        @Size(min = 2, max = 32, message = "Names must be within 2 to 32 characters")
        private String name;

        @NotBlank(message = "Password cannot be empty")
        @Size(min = 8, max = 32, message = "Password must be within 8 to 32 characters")
        @Pattern(
                regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d]{8,}$",
                message = "Password is not eight characters, at least one uppercase letter, one lowercase letter and one number"
        )
        private String password;
    }

    @Data
    @AllArgsConstructor
    public static class LoginForm {

        @NotBlank(message = "E-mail cannot be empty")
        @Email(message = "Invalid E-mail")
        private String email;

        @NotBlank(message = "Password cannot be empty")
        private String password;
    }

    @Data
    @AllArgsConstructor
    public static class ForgotPasswordForm {
        @NotBlank(message = "E-mail cannot be empty")
        @Email(message = "Invalid E-mail")
        private String email;
    }

    @Data
    @AllArgsConstructor
    public static class ConfirmForgotForm {

        @NotBlank(message = "code cannot be empty")
        private String code;

        @NotBlank(message = "E-mail cannot be empty")
        @Email(message = "Invalid E-mail")
        private String email;

        @NotBlank(message = "Password cannot be empty")
        @Size(min = 8, max = 32, message = "Password must be within 8 to 32 characters")
        @Pattern(
                regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d]{8,}$",
                message = "Password is not eight characters, at least one uppercase letter, one lowercase letter and one number"
        )
        private String newPassword;
    }
}
