package com.yohan.studi.user;

import com.yohan.studi.exception.BadRequestException;
import com.yohan.studi.exception.InternalServerException;
import com.yohan.studi.exception.TooManyException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.yohan.studi.user.UserForms.*;
import java.util.HashMap;

@RestController
@RequestMapping("api/v1/auth/")
@Slf4j
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("register")
    public ResponseEntity<HashMap<String, Object>> registerUser(@RequestBody RegisterForm form) {
        HashMap<String, Object> res = new HashMap<>();
        boolean result = userService.registerUser(form);
        res.put("success", result);
        return ResponseEntity.ok(res);
    }

    @PostMapping("login")
    public ResponseEntity<HashMap<String, Object>> loginUser(@RequestBody LoginForm form) {
        HashMap<String, Object> res = new HashMap<>();
        String token = userService.loginUser(form);
        res.put("token", token);
        return ResponseEntity.ok(res);
    }

    @PostMapping("forgot")
    public ResponseEntity<HashMap<String, Object>> forgotPassword(@RequestBody ForgotPasswordForm form) {
        HashMap<String, Object> res = new HashMap<>();
        boolean result = userService.forgotPassword(form);
        res.put("success", result);
        return ResponseEntity.ok(res);
    }

    @PatchMapping("confirmforgot")
    public ResponseEntity<HashMap<String, Object>> confirmForgot(@RequestBody ConfirmForgotForm form) {
        HashMap<String, Object> res = new HashMap<>();
        boolean result = userService.confirmForgotPassword(form);
        res.put("success", result);
        return ResponseEntity.ok(res);
    }
}
