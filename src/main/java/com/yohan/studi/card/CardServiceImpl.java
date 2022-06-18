package com.yohan.studi.card;

import com.yohan.studi.exception.BadRequestException;
import com.yohan.studi.subject.Subject;
import com.yohan.studi.subject.SubjectService;
import com.yohan.studi.util.DateUtils;
import com.yohan.studi.util.FormValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
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
        log.info("Creating card {}", form);
        formValidator.validateForm(form);
        Subject subject = subjectService.getSubjectById(form.getSubjectId());
        Card newCard = new Card(form.getQuestion(), form.getAnswer(), subject);
        cardRepository.save(newCard);
        log.info("Card {} saved", form);
        return true;
    }

    @Override
    public List<CardDto> getBySubjectId(Integer subjectId) {
        log.info("Getting cards with subject id {}", subjectId);
        Subject subject = subjectService.getSubjectById(subjectId);
        List<Card> cards = cardRepository.getCardsBySubject(subject.getId());
        log.info("{} cards found", cards.size());
        return cards.stream().map(CardDto::new).toList();
    }

    @Override
    public boolean changeCard(Integer cardId, CardForms.ChangeCardForm form) {
        log.info("Changing card id {} to fields {}", cardId, form);
        Card oldCard = getCardById(cardId);
        subjectService.getSubjectById(oldCard.getSubject().getId());
        formValidator.validateForm(form);
        oldCard.setAnswer(form.getAnswer());
        oldCard.setQuestion(form.getQuestion());
        cardRepository.save(oldCard);
        log.info("Card id {} was saved with fields {}", cardId, form);
        return true;
    }

    @Override
    public boolean pushDueDate(Integer id) {
        log.info("Pushing due date of card {}", id);
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
        LocalDate ldt = DateUtils.dateToLocalDate(card.getDueDate());
        ldt = ldt.plusDays((card.getLevel() * MULTIPLY) + OFFSET);
        card.setDueDate(DateUtils.localDateToDate(ldt));
        card.setLevel(card.getLevel() + 1);

        cardRepository.save(card);
        log.info("Pushing due date of card {} to date {}", id, ldt);
        return true;
    }

    @Override
    public boolean deleteCard(Integer id) {
        log.info("deleting card {}", id);
        Card card = getCardById(id);
        subjectService.getSubjectById(card.getSubject().getId());
        cardRepository.delete(card);
        log.info("card {} deleted", id);
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
