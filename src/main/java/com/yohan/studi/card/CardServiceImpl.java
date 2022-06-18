package com.yohan.studi.card;

import com.yohan.studi.exception.BadRequestException;
import com.yohan.studi.subject.Subject;
import com.yohan.studi.subject.SubjectService;
import com.yohan.studi.util.FormValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class CardServiceImpl implements CardService {

    private final CardRepository cardRepository;
    private final SubjectService subjectService;
    private final FormValidator formValidator;

    @Override
    public boolean createCard(CardForms.CreateCardForm form) {
        formValidator.validateForm(form);
        Subject subject = subjectService.getSubjectById(form.getSubjectId());
        Card newCard = new Card(form.getQuestion(), form.getAnswer(), subject);
        cardRepository.save(newCard);
        return true;
    }

    @Override
    public List<CardDto> getBySubjectId(Integer subjectId) {
        Subject subject = subjectService.getSubjectById(subjectId);
        List<Card> cards = cardRepository.getCardsBySubject(subject.getId());
        return cards.stream().map(CardDto::new).toList();
    }

    @Override
    public boolean changeCard(Integer cardId, CardForms.ChangeCardForm form) {
        Card oldCard = getCardById(cardId);
        subjectService.getSubjectById(oldCard.getSubject().getId());
        formValidator.validateForm(form);
        oldCard.setAnswer(form.getAnswer());
        oldCard.setQuestion(form.getQuestion());
        cardRepository.save(oldCard);
        return true;
    }

    @Override
    public boolean pushDueDate(Integer id) {
        final int OFFSET = 1;
        final int MULTIPLY = 2;
        final int MAX_LEVEL = 92;
        Card card = getCardById(id);
        subjectService.getSubjectById(card.getSubject().getId());

        // if level is MAX_LEVEL, don't increment
        if( card.getLevel() == MAX_LEVEL) {
            return true;
        }

        // add by days
        LocalDate ldt = convertToLocalDateViaInstant(card.getDueDate());
        ldt = ldt.plusDays((card.getLevel() * MULTIPLY) + OFFSET);
        card.setDueDate(convertToDateViaInstant(ldt));
        card.setLevel(card.getLevel() + 1);

        cardRepository.save(card);
        return true;
    }

    /**
     * Converts date to localDate
     *
     * @param dateToConvert date to convert
     * @return localDate format date
     */
    private LocalDate convertToLocalDateViaInstant(Date dateToConvert) {
        return dateToConvert.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
    }

    /**
     * Converts localDate to date
     * @param dateToConvert localDate to convert
     * @return date
     */
    private Date convertToDateViaInstant(LocalDate dateToConvert) {
        return java.util.Date.from(dateToConvert.atStartOfDay()
                .atZone(ZoneId.systemDefault())
                .toInstant());
    }

    @Override
    public boolean deleteCard(Integer id) {
        Card card = getCardById(id);
        subjectService.getSubjectById(card.getSubject().getId());
        cardRepository.delete(card);
        return true;
    }

    /**
     * Finds card by its id. if not found, an BadRequestException is called
     *
     * @param cardId id of card
     * @return card
     */
    private Card getCardById(Integer cardId) {
        return cardRepository
                .findById(cardId)
                .orElseThrow(() -> new BadRequestException("Card with id " + cardId + " not found"));
    }
}
