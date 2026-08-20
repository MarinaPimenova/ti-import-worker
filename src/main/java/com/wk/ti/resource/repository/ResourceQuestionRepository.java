package com.wk.ti.resource.repository;

import com.wk.ti.resource.model.ResourceQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ResourceQuestionRepository extends JpaRepository<ResourceQuestion, Long> {
}
