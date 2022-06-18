package com.yohan.studi.card;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CardRepository extends JpaRepository<Card, Integer> {
    @Query("select c from Card c WHERE c.subject.id = ?1")
    List<Card> getCardsBySubject(Integer subjectId);
}
