package com.yohan.studi.card;

import java.util.List;

public interface CardService {
    boolean createCard(CardForms.CreateCardForm form);
    List<CardDto> getBySubjectId(Integer subjectId);

    boolean changeCard(Integer cardId, CardForms.ChangeCardForm form);

    boolean pushDueDate(Integer id);

    boolean deleteCard(Integer id);
}
