package com.yohan.studi.integration;

import com.yohan.studi.card.CardDto;
import com.yohan.studi.card.CardService;
import com.yohan.studi.subject.SubjectForms;
import com.yohan.studi.subject.SubjectRepository;
import com.yohan.studi.subject.SubjectService;
import com.yohan.studi.user.UserForms;
import com.yohan.studi.user.UserRepository;
import com.yohan.studi.user.UserService;
import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static io.zonky.test.db.AutoConfigureEmbeddedDatabase.RefreshMode.AFTER_EACH_TEST_METHOD;
import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureEmbeddedDatabase(refresh = AFTER_EACH_TEST_METHOD)
public class IT_CardService {

    @Autowired
    private UserService userService;

    @Autowired
    private SubjectService subjectService;

    @Autowired
    private CardService cardService;

    @BeforeEach
    public void beforeEach() {
        userService.registerUser(new UserForms.RegisterForm("test@gmail.com", "test", "Yohan123"));

        UserDetails userDetails = userService.loadUserByUsername("test@gmail.com");

        UsernamePasswordAuthenticationToken
                authentication = new UsernamePasswordAuthenticationToken(
                userDetails, null,
                userDetails == null ?
                        List.of() : userDetails.getAuthorities()
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        // create subject
        SubjectForms.SubjectForm subjectForm = new SubjectForms.SubjectForm("Hello World");
        subjectService.createSubject(subjectForm);
    }

    /*
     * createCard():
     *  - empty form throws error
     *  -
     */

    @Test
    public void getCardsBySubjectIdWithEmptyList_ReturnsEmptyList() {
        // get list of cards
        List<CardDto> cardDtos = cardService.getAllCardsBySubject(1);

        // assert empty list
        assertEquals(0, cardDtos.size());
    }

    @Test
    public void getCardsBySubjectIdTwoCards_ReturnsTwo() {
        // create two cards
        fail();
    }
}
