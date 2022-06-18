package com.yohan.studi.integration;

import com.icegreen.greenmail.configuration.GreenMailConfiguration;
import com.icegreen.greenmail.junit5.GreenMailExtension;
import com.icegreen.greenmail.util.ServerSetupTest;
import com.yohan.studi.card.Card;
import com.yohan.studi.card.CardForms;
import com.yohan.studi.card.CardRepository;
import com.yohan.studi.card.CardService;
import com.yohan.studi.exception.BadRequestException;
import com.yohan.studi.exception.ForbiddenException;
import com.yohan.studi.subject.Subject;
import com.yohan.studi.subject.SubjectForms;
import com.yohan.studi.subject.SubjectRepository;
import com.yohan.studi.subject.SubjectService;
import com.yohan.studi.user.User;
import com.yohan.studi.user.UserForms;
import com.yohan.studi.user.UserRepository;
import com.yohan.studi.user.UserService;
import com.yohan.studi.util.DateUtils;
import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import static io.zonky.test.db.AutoConfigureEmbeddedDatabase.RefreshMode.AFTER_EACH_TEST_METHOD;
import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureEmbeddedDatabase(refresh = AFTER_EACH_TEST_METHOD)
public class IT_CardService {

    @Autowired
    private UserService userService;

    @Autowired
    private SubjectService subjectService;

    @Autowired
    private CardService cardService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SubjectRepository subjectRepository;

    @Autowired
    private CardRepository cardRepository;

    @RegisterExtension
    static GreenMailExtension greenMail = new GreenMailExtension(ServerSetupTest.SMTP)
            .withConfiguration(GreenMailConfiguration.aConfig().withUser("duke", "springboot"))
            .withPerMethodLifecycle(false);

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
     *  - Empty form throws
     *  - Empty subject throws
     *  - Subject author not user throws
     *  - True if everything else succeed
     */
    @Test
    public void createCardEmptyForm_Throws() {
        assertThrows(BadRequestException.class, () -> cardService.createCard(null));
    }

    @Test
    public void createCardEmptySubject_Throws() {
        CardForms.CreateCardForm cardForm = new CardForms.CreateCardForm("Hello", "World", 2);
        assertThrows(BadRequestException.class, () -> cardService.createCard(cardForm));
    }

    @Test
    public void createCardNotSubjectAuthor_Throws() {
        User user = userRepository.save(new User("hello@gmail.com", "bro", "Yohan123"));
        Subject subject = subjectRepository.save(new Subject("Hi", user));
        CardForms.CreateCardForm cardForm = new CardForms.CreateCardForm("Hello", "World", subject.getId());
        assertThrows(ForbiddenException.class, () -> cardService.createCard(cardForm));
    }

    @Test
    public void createCardValid_ReturnsTrue() {
        CardForms.CreateCardForm cardForm = new CardForms.CreateCardForm("Hello", "World", 1);
        assertTrue(cardService.createCard(cardForm));
    }

    /*
     * bySubjectId():
     *  - Non-existing subject id throws BadException class
     *  - Empty list returns 0
     *  - Two created cards returns 2
     */
    @Test
    public void getBySubjectIdNotExists_ThrowsBadException() {
        assertThrows(BadRequestException.class, () -> cardService.getBySubjectId(2));
    }

    @Test
    public void getBySubjectEmptyList_Size0() {
        assertEquals(0, cardService.getBySubjectId(1).size());
    }

    @Test
    public void getBySubjectTwoInList_Size2() {
        cardService.createCard(new CardForms.CreateCardForm("Hello", "World", 1));
        cardService.createCard(new CardForms.CreateCardForm("Hello2", "World2", 1));
        assertEquals(2, cardService.getBySubjectId(1).size());
    }

    /*
    * changeCard():
    *  - Empty form throws
    *  - Empty Card id throws
    *  - Subject not author throws
    *  - True if everything else succeed
    */
    @Test
    public void changeCardEmptyForm_Throws() {
        assertThrows(BadRequestException.class, () -> cardService.changeCard(1, null));
    }

    @Test
    public void changeCardNonExistingCardId_Throws() {
        assertThrows(BadRequestException.class, () -> cardService.changeCard(1, new CardForms.ChangeCardForm("Hello", "World")));
    }

    @Test
    public void changeCardNotAuthorOfSubject_Throws() {
        User user = userRepository.save(new User("hello@gmail.com", "bro", "Yohan123"));
        Subject subject = subjectRepository.save(new Subject("Hi", user));
        Card card = cardRepository.save(new Card("Hello", "World", subject));
        assertThrows(ForbiddenException.class, () -> cardService.changeCard(card.getId(), new CardForms.ChangeCardForm("Hello", "World")));
    }

    @Test
    public void changeCardValid_ReturnsTrue() {
        cardService.createCard(new CardForms.CreateCardForm("Hello", "World", 1));
        assertTrue(cardService.changeCard(1, new CardForms.ChangeCardForm("Hello", "World")));
    }

    /*
    * pushDueDate():
    *  - empty card id throws
    *  - subject not author throws
    *  - level 0 pushes by 1 day & returns true
    *  - level 1 pushes by 4 days & returns true
    *  - level 2 pushes by 9 days & returns true
    */
    @Test
    public void pushDueDateEmptyId_Throws() {
        assertThrows(BadRequestException.class, () -> cardService.pushDueDate(1));
    }

    @Test
    public void pushDueDateNotAuthor_Throws() {
        User user = userRepository.save(new User("hello@gmail.com", "bro", "Yohan123"));
        Subject subject = subjectRepository.save(new Subject("Hi", user));
        Card card = cardRepository.save(new Card("Hello", "World", subject));
        assertThrows(ForbiddenException.class, () -> cardService.pushDueDate(card.getId()));
    }

    @Test
    public void pushDueDateLevel0_Add1Day() {
        cardService.createCard(new CardForms.CreateCardForm("Hello", "World", 1));
        cardService.pushDueDate(1);

        Card card = cardRepository.findById(1).orElseThrow();
        Date today = new Date();
        assertEquals(DateUtils.dateToLocalDate(today).plusDays(1), DateUtils.dateToLocalDate(card.getDueDate()));
    }

    @Test
    public void pushDueDateLevel1_Add4Day() {
        cardService.createCard(new CardForms.CreateCardForm("Hello", "World", 1));
        cardService.pushDueDate(1);
        cardService.pushDueDate(1);

        Card card = cardRepository.findById(1).orElseThrow();
        Date today = new Date();
        assertEquals(DateUtils.dateToLocalDate(today).plusDays(4), DateUtils.dateToLocalDate(card.getDueDate()));
    }

    @Test
    public void pushDueDateLevel2_Add9Day() {
        cardService.createCard(new CardForms.CreateCardForm("Hello", "World", 1));
        cardService.pushDueDate(1);
        cardService.pushDueDate(1);
        cardService.pushDueDate(1);

        Card card = cardRepository.findById(1).orElseThrow();
        Date today = new Date();
        assertEquals(DateUtils.dateToLocalDate(today).plusDays(9), DateUtils.dateToLocalDate(card.getDueDate()));
    }

    /*
     * deleteCard():
     *  - empty card id throws
     *  - subject not author throws
     *  - delete returns true
     */
    @Test
    public void deleteCardEmptyId_Throws() {
        assertThrows(BadRequestException.class, () -> cardService.deleteCard(1));
    }

    @Test
    public void deleteCardNotAuthor_Throws() {
        User user = userRepository.save(new User("hello@gmail.com", "bro", "Yohan123"));
        Subject subject = subjectRepository.save(new Subject("Hi", user));
        Card card = cardRepository.save(new Card("Hello", "World", subject));
        assertThrows(ForbiddenException.class, () -> cardService.deleteCard(card.getId()));
    }

    @Test
    public void deleteCardValid_ReturnsTrue() {
        cardService.createCard(new CardForms.CreateCardForm("Hello", "World", 1));
        assertTrue(cardService.deleteCard(1));
    }
}
