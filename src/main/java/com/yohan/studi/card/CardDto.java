package com.yohan.studi.card;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CardDto {
    private Integer id;
    private String question;
    private String answer;
    private Integer subjectId;

    public CardDto(Card card) {
        this.id = card.getId();
        this.question = card.getQuestion();
        this.answer = card.getAnswer();
        this.subjectId = card.getSubject().getId();
    }
}
