package com.yohan.studi.subject;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubjectDto {
    private Integer id;
    private String name;
    private Integer userId;

    public SubjectDto(Subject subject) {
        id = subject.getId();
        name = subject.getName();
        userId = subject.getUser().getId();
    }
}
