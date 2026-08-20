package com.wk.ti.question.repository;

import com.wk.ti.question.model.Question;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
@SuppressWarnings({"SqlResolve", "SqlSignature"})
public interface QuestionRepository extends JpaRepository<Question, Long> {
}
