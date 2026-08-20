package com.wk.ti.resource.model;

import com.wk.ti.question.model.BaseEntity;
import com.wk.ti.question.model.Question;
import jakarta.persistence.*;
import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "question_resource",
        uniqueConstraints = {@UniqueConstraint(columnNames = {"question_id", "resource_id"})},
        schema = "knowledge")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResourceQuestion extends BaseEntity {
    @EmbeddedId
    private ResourceQuestionKey resourceQuestionKey;

    @ManyToOne
    @MapsId("questionId")
    @JoinColumn(name = "question_id")
    private Question question;

    @ManyToOne
    @MapsId("resourceId")
    @JoinColumn(name = "resource_id")
    private Resource resource;
}
