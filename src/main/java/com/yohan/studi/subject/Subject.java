package com.yohan.studi.subject;

import com.yohan.studi.user.User;
import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "subjects")
@EqualsAndHashCode
@ToString
@NoArgsConstructor
public class Subject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    @Setter
    @Column(name="id")
    private Integer id;

    @Getter
    @Setter
    @Column(name="name")
    private String name;

    @ManyToOne
    @JoinColumn(name="user_id", nullable = false)
    @Getter
    @Setter
    private User user;

    public Subject(String name, User user) {
        this.name = name;
        this.user = user;
    }
}
