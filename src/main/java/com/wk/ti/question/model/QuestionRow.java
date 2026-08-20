package com.wk.ti.question.model;

import java.util.List;

public record QuestionRow(

        String question,

        String shortAnswer,

        String questionLevel,

        List<ResourceRequest> resources
) {
}
