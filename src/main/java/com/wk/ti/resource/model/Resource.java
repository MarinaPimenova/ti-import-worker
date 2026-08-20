package com.wk.ti.resource.model;

import com.wk.ti.question.model.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@EqualsAndHashCode(callSuper = true)
@SuppressWarnings("JpaDataSourceORMInspection")
@Entity
@Table(name = "resource", schema = "knowledge")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Resource extends BaseEntity {
    @Id
    @GeneratedValue(generator = "resource_id_seq_gen", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "resource_id_seq_gen", sequenceName = "knowledge.resource_id_seq",
            allocationSize = 100)
    private Long id;
    @Column(name = "resource_url")
    private String resourceUrl;
    @Column(name = "description")
    private String description;
}
