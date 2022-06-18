package com.yohan.studi.unit;

import com.yohan.studi.card.CardForms;
import com.yohan.studi.exception.BadRequestException;
import com.yohan.studi.util.FormValidator;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

@Slf4j
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class Unit_CardForms {

    private FormValidator formValidator;

    @BeforeAll
    public void before() {
        formValidator = new FormValidator();
    }

    @Test
    public void createCardEmptyQuestion_Throws() {
        CardForms.CreateCardForm form = new CardForms.CreateCardForm("", "world", 1);
        Assertions.assertThrows(BadRequestException.class, () -> formValidator.validateForm(form));
    }

    @Test
    public void createCardEmptyAnswer_Throws() {
        CardForms.CreateCardForm form = new CardForms.CreateCardForm("hello", "", 1);
        Assertions.assertThrows(BadRequestException.class, () -> formValidator.validateForm(form));
    }

    @Test
    public void createCardNullSubjectId_Throws() {
        CardForms.CreateCardForm form = new CardForms.CreateCardForm("hello", "world", null);
        Assertions.assertThrows(BadRequestException.class, () -> formValidator.validateForm(form));
    }

    @Test
    public void createCardValidForm_Passes() {
        CardForms.CreateCardForm form = new CardForms.CreateCardForm("hello", "world", 1);
        Assertions.assertDoesNotThrow(() -> formValidator.validateForm(form));
    }

    @Test
    public void changeCardEmptyQuestion_Throws() {
        CardForms.ChangeCardForm form = new CardForms.ChangeCardForm("", "world");
        Assertions.assertThrows(BadRequestException.class, () -> formValidator.validateForm(form));
    }

    @Test
    public void changeCardEmptyAnswer_Throws() {
        CardForms.ChangeCardForm form = new CardForms.ChangeCardForm("hello", "");
        Assertions.assertThrows(BadRequestException.class, () -> formValidator.validateForm(form));
    }

    @Test
    public void changeCardValidForm_Passes() {
        CardForms.ChangeCardForm form = new CardForms.ChangeCardForm("hello", "world");
        Assertions.assertDoesNotThrow(() -> formValidator.validateForm(form));
    }
}
