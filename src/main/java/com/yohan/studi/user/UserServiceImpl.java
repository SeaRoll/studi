package com.yohan.studi.user;

import com.yohan.studi.cache.RateLimiterService;
import com.yohan.studi.email.Email;
import com.yohan.studi.email.EmailService;
import com.yohan.studi.exception.BadRequestException;
import com.yohan.studi.exception.InternalServerException;
import com.yohan.studi.exception.TooManyException;
import com.yohan.studi.security.JwtTokenUtil;
import com.yohan.studi.util.FormValidator;
import io.github.bucket4j.Bucket;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.yohan.studi.user.UserForms.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.MessagingException;
import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenUtil jwtTokenUtil;
    private final EmailService emailService;
    private final RateLimiterService rateLimiterService;

    private final FormValidator formValidator;

    /**
     * gets a user by email. if there are no e-mails found,
     * it throws a BadRequestException
     *
     * @param email email of user to find
     * @return a user
     */
    public User getUserByEmail(String email) {
        return userRepository
                .getUserByEmail(email)
                .orElseThrow(() -> {
                    log.warn("User with e-mail {} does not exist", email);
                    return new BadRequestException(String.format("User with e-mail %s does not exist", email));
                });
    }

    /**
     * Generates a 7 length alphanumeric code.
     * used for when generating forgot password token
     *
     * @return generated code
     */
    private String generateAlphaNumericCode() {
        int leftLimit = 48; // numeral '0'
        int rightLimit = 122; // letter 'z'
        int targetStringLength = 7;
        Random random = new Random();

        return random.ints(leftLimit, rightLimit + 1)
                .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                .limit(targetStringLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = getUserByEmail(username);
        Set<GrantedAuthority> authorities = new HashSet<>();
        return new org
                .springframework
                .security
                .core
                .userdetails
                .User(user.getEmail(), user.getHashedPassword(), authorities);
    }

    @Override
    public boolean registerUser(RegisterForm form) {
        // validate form
        formValidator.validateForm(form);

        // get fields
        String email = form.getEmail().toLowerCase(Locale.ROOT);
        String name = form.getName().toLowerCase(Locale.ROOT);
        String password = form.getPassword();

        log.info("Creating user with email: {}, name: {}", email, name);
        // search user by email
        Optional<User> user = userRepository.getUserByEmail(email);

        // throw if user with email already exists
        if(user.isPresent()) {
            log.warn("User with e-mail {} already exists", email);
            throw new BadRequestException("User with e-mail already exists");
        }

        // create user
        try {
            User createdUser = new User(email, name, passwordEncoder.encode(password));
            userRepository.save(createdUser);
        } catch(Exception e) {
            log.error("SQL Error {} was thrown", e.getMessage());
            throw new InternalServerException("Internal Server error");
        }

        // send registration e-mail
        Email emailTemplate = new Email();
        emailTemplate.setTo(email);
        emailTemplate.setSubject("STUDI, Hello " + name);
        emailTemplate.setTemplate("register-email.html");
        Map<String, Object> properties = new HashMap<>();
        properties.put("name", name);
        emailTemplate.setProperties(properties);

        try {
            emailService.sendHtmlMessage(emailTemplate);
        } catch (MessagingException e) {
            log.error(e.getMessage());
            throw new InternalServerException("Internal server error");
        }

        log.info("Created user with e-mail {}", email);
        return true;
    }

    @Override
    public String loginUser(LoginForm form) {
        // validate form
        formValidator.validateForm(form);

        // get fields
        String email = form.getEmail().toLowerCase(Locale.ROOT);
        String password = form.getPassword();

        // check if user has tried too many times
        Bucket bucket = rateLimiterService.resolveBucketLogin(email);
        if(!bucket.tryConsume(1)) {
            throw new TooManyException("Too many requests");
        }

        // find user
        User user = getUserByEmail(email);

        // throw if passwordEncoder cannot match
        if (!passwordEncoder.matches(password, user.getHashedPassword())) {
           log.warn("User with e-mail {} password does not match", email);
           throw new BadRequestException("Password does not match");
        }

        // build & return jwt
        return jwtTokenUtil.buildJwt(user.getId(), user.getEmail());
    }

    @Override
    public boolean confirmForgotPassword(ConfirmForgotForm form) {
        // validate form
        formValidator.validateForm(form);

        // get fields
        String email = form.getEmail().toLowerCase(Locale.ROOT);
        String code = form.getCode();
        String newPassword = form.getNewPassword();

        log.info("Confirming user {} with code {}", email, code);
        // get user
        User user = getUserByEmail(email);

        // if reset token is empty, then user needs to forgot password
        if(user.getResetToken().equals("")) {
            log.warn("User {} has no code", email);
            throw new BadRequestException("There are no reset code");
        }

        // if code does not match
        if(!user.getResetToken().equals(code)) {
            log.warn("Code {} does not match {}. sending error", code, user.getResetToken());
            throw new BadRequestException("Code does not match");
        }

        // if expiration date is over
        if(new Date().compareTo(user.getResetTokenExpirationDate()) > 0) {
            log.warn("Date now {} is higher than expiration {}", new Date(), user.getResetTokenExpirationDate());
            throw new BadRequestException("Expiration date is over");
        }

        // set password to new password
        user.setHashedPassword(passwordEncoder.encode(newPassword));

        // remove token
        user.setResetToken("");

        // save user
        userRepository.save(user);

        log.info("User {} changed password successfully", email);
        return true;
    }

    @Override
    public boolean forgotPassword(ForgotPasswordForm form) {
        // validate form
        formValidator.validateForm(form);

        // get fields
        String email = form.getEmail().toLowerCase(Locale.ROOT);

        // check if user has tried too many times
        Bucket bucket = rateLimiterService.resolveBucketForgot(email);
        if(!bucket.tryConsume(1)) {
            throw new TooManyException("Too many requests");
        }

        log.info("User {} forgot password. Generating forgot token", email);

        // find user
        User user = getUserByEmail(email);

        // generate a verification code
        String generatedToken = generateAlphaNumericCode();

        // set user's password forgot to generated token
        user.setResetToken(generatedToken);

        // set user's expiration date to 30 minutes
        Date dt = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(dt);
        c.add(Calendar.MINUTE, 30);
        dt = c.getTime();
        user.setResetTokenExpirationDate(dt);

        // save user
        userRepository.save(user);

        // send email
        Email emailTemplate = new Email();
        emailTemplate.setTo(email);
        emailTemplate.setSubject("STUDI, Forgot password code");
        emailTemplate.setTemplate("forgot-email.html");
        Map<String, Object> properties = new HashMap<>();
        properties.put("name", user.getName());
        properties.put("code", generatedToken);
        emailTemplate.setProperties(properties);

        try {
            emailService.sendHtmlMessage(emailTemplate);
        } catch (MessagingException e) {
            log.error(e.getMessage());
            throw new InternalServerException("Internal server error");
        }

        log.info("Generated and E-mail sent to user {}", email);
        return true;
    }
}
