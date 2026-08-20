package com.wk.ti.qlevel.model;

import com.wk.ti.question.model.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@EqualsAndHashCode(callSuper = true)
@SuppressWarnings("JpaDataSourceORMInspection")
@Entity
@Table(name = "question_level", schema = "knowledge")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuestionLevel extends BaseEntity {
    @Id
    @GeneratedValue(generator = "question_level_id_seq_gen", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "question_level_id_seq_gen", sequenceName = "knowledge.question_level_id_seq",
            allocationSize = 10)
    private Long id;
    @Column(name = "code")
    private String code;
    @Column(name = "description")
    private String description;
}
