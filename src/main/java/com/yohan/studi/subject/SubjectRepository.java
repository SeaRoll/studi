package com.yohan.studi.subject;

import com.yohan.studi.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SubjectRepository extends JpaRepository<Subject, Integer> {
    List<Subject> findAllByUser(User user);
}
