package com.wk.ti.question.model;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import java.io.Serializable;
import java.time.OffsetDateTime;

import static com.wk.ti.user.service.UserDetailExtractor.getUser;

@Data
@MappedSuperclass
public abstract class BaseEntity implements Serializable {
    @CreatedBy
    @Column(name = "created_by", updatable = false)
    protected String createdBy;

    @CreatedDate
    @Column(name = "created_date", updatable = false)
    protected OffsetDateTime createdDate;

    @Column(name = "updated_by")
    @LastModifiedBy
    protected String updatedBy;

    @Column(name = "modified_date")
    @LastModifiedDate
    protected OffsetDateTime updatedDate;

    @PrePersist
    public void prePersist() {
        if (createdDate == null) {
            createdDate = OffsetDateTime.now();
        }
        if (createdBy == null) {
            createdBy = getUser();
        }

        updatedDate = OffsetDateTime.now();
        updatedBy = getUser();

    }
}
