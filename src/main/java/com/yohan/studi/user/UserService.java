package com.yohan.studi.user;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import com.yohan.studi.user.UserForms.*;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public interface UserService extends UserDetailsService {

    /**
     * Uses SecurityContextHolder which comes from filter
     * to return a user.
     *
     * @return a user from security
     */
    User getUserByContext();

    /**
     * Loads a User with UserDetails by giving e-mail
     *
     * @param username e-mail of user
     * @return user
     * @throws UsernameNotFoundException if user is not found
     */
    @Override
    UserDetails loadUserByUsername(String username) throws UsernameNotFoundException;

    /**
     * Registers a user. if an existing e-mail is found, a BadRequest is called.
     * then encodes password and creates a user
     *
     * @param form register form
     * @return true if register was successful
     */
    boolean registerUser(RegisterForm form);

    /**
     * Logins a user. if user is not found, a BadRequestException is called.
     * if password does not match, BadRequestException is called.
     * if everything matches, a jwt is returned
     *
     * @param form login form
     * @return jwt token
     */
    String loginUser(LoginForm form);

    /**
     * Confirm user forgot password
     *
     * @param form confirm forgot password form
     * @return true if successful
     */
    boolean confirmForgotPassword(ConfirmForgotForm form);

    /**
     * Generates a forgot password token and expiration date on user
     * and then sends an e-mail telling the user to reset password
     *
     * @param form forgot password form
     * @return true if successful
     */
    boolean forgotPassword(ForgotPasswordForm form);
}
