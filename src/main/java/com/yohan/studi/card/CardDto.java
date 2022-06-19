package com.yohan.studi.card;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CardDto {
    private Integer id;
    private String question;
    private String answer;
    private Date dueDate;
    private Integer subjectId;

    public CardDto(Card card) {
        this.id = card.getId();
        this.question = card.getQuestion();
        this.answer = card.getAnswer();
        this.dueDate = card.getDueDate();
        this.subjectId = card.getSubject().getId();
    }
}
