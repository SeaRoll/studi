package com.yohan.studi.subject;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

public class SubjectForms {

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SubjectForm {
        @NotBlank(message = "name cannot be empty")
        private String name;
    }
}
