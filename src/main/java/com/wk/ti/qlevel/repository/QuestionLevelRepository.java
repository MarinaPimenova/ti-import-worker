package com.wk.ti.qlevel.repository;

import com.wk.ti.qlevel.model.QuestionLevel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface QuestionLevelRepository extends JpaRepository<QuestionLevel, Long> {
    @Query(value = """
            select id from knowledge.question_level where code = :levelCode
            """, nativeQuery = true)
    Long findByCode(@Param("levelCode") String code);
}
