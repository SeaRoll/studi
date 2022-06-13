package com.yohan.studi.integration;

import com.yohan.studi.exception.BadRequestException;
import com.yohan.studi.exception.ForbiddenException;
import com.yohan.studi.subject.Subject;
import com.yohan.studi.subject.SubjectDto;
import com.yohan.studi.subject.SubjectForms.*;
import com.yohan.studi.subject.SubjectRepository;
import com.yohan.studi.subject.SubjectService;
import com.yohan.studi.user.User;
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
public class IT_SubjectService {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SubjectRepository subjectRepository;

    @Autowired
    private SubjectService subjectService;

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
    }

    @Test
    public void createSubjectValid_Works() {
        SubjectForm subjectForm = new SubjectForm("Hello World");
        assertTrue(subjectService.createSubject(subjectForm));
    }

    @Test
    public void readSubjects_Works() {
        createSubjectValid_Works();
        List<SubjectDto> dtos = subjectService.readSubjects();
        assertEquals(1, dtos.size());
        assertEquals("Hello World", dtos.get(0).getName());
    }

    @Test
    public void patchNonExisting_Throws() {
        assertThrows(BadRequestException.class, () -> subjectService.patchSubject(1, new SubjectForm("2")));
    }

    @Test
    public void patchNonAuthor_Throws() {
        User created = userRepository.save(new User("test2@gmail.com", "yohan", "123"));
        Subject subject = subjectRepository.save(new Subject("Hello", created));
        assertThrows(ForbiddenException.class, () -> subjectService.patchSubject(subject.getId(), new SubjectForm("Hello World")));
    }

    @Test
    public void patchValid_Works() {
        createSubjectValid_Works();
        List<SubjectDto> dtos = subjectService.readSubjects();
        assertTrue(subjectService.patchSubject(dtos.get(0).getId(), new SubjectForm("Hello World")));
    }

    @Test
    public void deleteNonExisting_Throws() {
        assertThrows(BadRequestException.class, () -> subjectService.deleteSubject(1));
    }

    @Test
    public void deleteNonAuthor_Throws() {
        User created = userRepository.save(new User("test2@gmail.com", "yohan", "123"));
        Subject subject = subjectRepository.save(new Subject("Hello", created));
        assertThrows(ForbiddenException.class, () -> subjectService.deleteSubject(subject.getId()));
    }

    @Test
    public void deleteValid_Works() {
        createSubjectValid_Works();
        List<SubjectDto> dtos = subjectService.readSubjects();
        assertTrue(subjectService.deleteSubject(dtos.get(0).getId()));
    }
}
