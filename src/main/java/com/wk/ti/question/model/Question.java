package com.wk.ti.question.model;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

@EqualsAndHashCode(callSuper = true)
@SuppressWarnings("JpaDataSourceORMInspection")
@Entity
@Table(name = "question", schema = "knowledge")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Question extends BaseEntity implements Serializable {
    @Id
    @GeneratedValue(generator = "question_id_seq_gen", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "question_id_seq_gen", sequenceName = "knowledge.question_id_seq",
            allocationSize = 100)
    private Long id;
    @Column(name = "question_level_id")
    private Long questionLevelId;
    @Column(name = "question")
    private String question;
    @Column(name = "short_answer")
    private String shortAnswer;
    @Column(name = "detailed_answer")
    private String detailedAnswer;

    @Transient
    public static Question of(QuestionRow row, Long qLevelId) {
        return Question.builder()
                .question(row.question())
                .shortAnswer(row.shortAnswer())
                .questionLevelId(qLevelId)
                .build();
    }
}