package com.wk.ti.resource.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Embeddable
public class ResourceQuestionKey implements Serializable {
    @Column(name = "question_id")
    Long questionId;
    @Column(name="resource_id")
    Long resourceId;
}
