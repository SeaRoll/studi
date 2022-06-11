package com.yohan.studi.unit;

import com.yohan.studi.exception.BadRequestException;
import com.yohan.studi.subject.SubjectForms;
import com.yohan.studi.user.UserForms;
import com.yohan.studi.util.FormValidator;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Slf4j
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class Unit_SubjectForms {

    private FormValidator formValidator;

    @BeforeAll
    public void before() {
        formValidator = new FormValidator();
    }

    @Test
    public void subjectFormCorrect_Works() {
        // given valid form
        SubjectForms.SubjectForm form = new SubjectForms.SubjectForm("hello world");

        // does not throw
        assertDoesNotThrow(() -> formValidator.validateForm(form));
    }

    @Test
    public void subjectFormInvalid_Throws() {
        // given valid form
        SubjectForms.SubjectForm form = new SubjectForms.SubjectForm("");

        // does not throw
        assertThrows(BadRequestException.class, () -> formValidator.validateForm(form));
    }
}
