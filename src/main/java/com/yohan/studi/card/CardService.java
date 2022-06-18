package com.yohan.studi.card;

import java.util.List;

public interface CardService {


    /**
     * Creates a card from form
     *
     * @param form form of card to create
     * @return true if successfully created
     */
    boolean createCard(CardForms.CreateCardForm form);

    /**
     * Gets all cards by subject
     *
     * @param subjectId id of subject
     * @return cards for subject
     */
    List<CardDto> getAllCardsBySubject(Integer subjectId);

    /**
     * Gets all cards by subject and if due date
     * is lower than current date
     *
     * @param subjectId id of subject
     * @return cards for subject and due date passed
     */
    List<CardDto> getAllCardsBySubjectAndDueDate(Integer subjectId);

    /**
     * Sets new due date for card with set difficulty.
     * It as four difficulties:
     *  - 0: reset card
     *  - 1: hard
     *  - 2: medium
     *  - 3: easy
     *
     * @param cardId id of card
     * @param difficulty difficulty of card
     * @return true if successfully saved
     */
    boolean setCardNewDate(Integer cardId, Integer difficulty);
}
