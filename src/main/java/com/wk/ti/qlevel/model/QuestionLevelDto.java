package com.wk.ti.qlevel.model;

public record QuestionLevelDto(

        Long questionLevelId,
        String difficultyCode
) {
    public static QuestionLevelDto of(QuestionLevel ql) {
        return new QuestionLevelDto(
                ql.getId(),
                ql.getCode()
        );
    }
}
