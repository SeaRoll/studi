package com.yohan.studi.card;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class CardForms {

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CreateCardForm {
        @NotBlank(message = "Question can't be empty")
        private String question;

        @NotBlank(message = "Answer can't be empty")
        private String answer;

        @NotNull(message = "Subject id is required")
        private Integer subjectId;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ChangeCardForm {
        @NotBlank(message = "Question can't be empty")
        private String question;

        @NotBlank(message = "Answer can't be empty")
        private String answer;
    }
}
