package com.dlms.dto;

import lombok.Data;
import java.util.List;

@Data
public class TestSubmissionRequest {
    private Long applicationId;
    private int score;
    private List<AnswerDto> answers;

    @Data
    public static class AnswerDto {
        private Long questionId;
        private String selectedOption;
    }
}