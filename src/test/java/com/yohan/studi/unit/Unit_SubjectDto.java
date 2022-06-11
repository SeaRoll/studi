package com.yohan.studi.unit;

import com.yohan.studi.subject.Subject;
import com.yohan.studi.subject.SubjectDto;
import com.yohan.studi.user.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class Unit_SubjectDto {

    @Test
    public void subjectTransformsCorrectly() {
        // given objects
        User user = new User("test@gmail.com", "test", "password");
        user.setId(213);
        Subject subject = new Subject("Hello World", user);
        subject.setId(1);

        // transform to dto
        SubjectDto subjectDto = new SubjectDto(subject);

        // assert dto is working
        Assertions.assertEquals(1, subjectDto.getId());
        Assertions.assertEquals("Hello World", subjectDto.getName());
        Assertions.assertEquals(213, subjectDto.getUserId());
    }
}
