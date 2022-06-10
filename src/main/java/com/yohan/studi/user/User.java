package com.yohan.studi.user;

import lombok.*;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name="users")
@EqualsAndHashCode
@ToString
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    @Setter
    @Column(name="id")
    private Integer id;

    @Getter
    @Setter
    @Column(name="email")
    private String email;

    @Getter
    @Setter
    @Column(name="name")
    private String name;

    @Getter
    @Setter
    @ToString.Exclude
    @Column(name="hashed_password")
    private String hashedPassword;

    @Getter
    @Setter
    @Column(name="reset_token")
    private String resetToken;

    @Getter
    @Setter
    @Column(name="reset_token_expiration_timestamp")
    private Date resetTokenExpirationDate;

    public User(String email, String name, String hashedPassword) {
        this.email = email;
        this.name = name;
        this.hashedPassword = hashedPassword;
        this.resetToken = "";
        resetTokenExpirationDate = new Date();
    }
}
