package com.yohan.studi.card;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class CardServiceImpl implements CardService {

    private final CardRepository cardRepository;

    @Override
    public boolean createCard(CardForms.CreateCardForm form) {
        return false;
    }

    @Override
    public List<CardDto> getAllCardsBySubject(Integer subjectId) {
        return null;
    }

    @Override
    public List<CardDto> getAllCardsBySubjectAndDueDate(Integer subjectId) {
        return null;
    }

    @Override
    public boolean setCardNewDate(Integer cardId, Integer difficulty) {
        if(difficulty > 3 || difficulty < 0)
            throw new IllegalArgumentException("difficulty must be between 0 and 3");
        return false;
    }
}
