package com.yohan.studi.card;

import com.yohan.studi.subject.Subject;
import lombok.*;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "cards")
@EqualsAndHashCode
@ToString
@NoArgsConstructor
public class Card {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    @Getter
    @Setter
    private Integer id;

    @Getter
    @Setter
    @Column(name="question")
    private String question;

    @Getter
    @Setter
    @Column(name="answer")
    private String answer;

    @Getter
    @Setter
    @Column(name="due_date")
    private Date dueDate;

    @Getter
    @Setter
    @Column(name="level")
    private Integer level;

    @ManyToOne
    @JoinColumn(name="subject_id", nullable = false)
    @Getter
    @Setter
    private Subject subject;

    public Card(String question, String answer, Subject subject) {
        this.question = question;
        this.answer = answer;
        this.subject = subject;
        this.level = 0;
        this.dueDate = new Date();
    }
}
