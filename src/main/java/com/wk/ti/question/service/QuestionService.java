package com.wk.ti.question.service;

import com.wk.ti.qlevel.repository.QuestionLevelRepository;
import com.wk.ti.question.model.Question;
import com.wk.ti.question.model.QuestionRow;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class QuestionService {
    @PersistenceContext
    private EntityManager entityManager;
    private final QuestionLevelRepository questionLevelRepository;

    public QuestionService(QuestionLevelRepository questionLevelRepository) {
        this.questionLevelRepository = questionLevelRepository;
    }

    @Transactional
    public int bulkInsert(List<Question> questions) {
        int batchSize = 200; // Set the desired batch size

        for (int i = 0; i < questions.size(); i++) {

            entityManager.persist(questions.get(i));

            if (i % batchSize == 0 && i > 0) {
                entityManager.flush();
                entityManager.clear();
            }
        }
        // Flush and clear the EntityManager to ensure all entities are persisted
        entityManager.flush();
        entityManager.clear();
        return questions.size();
    }

    public List<Question> generate(List<QuestionRow> questionRows) {
        List<Question> result = new ArrayList<>();
        for (QuestionRow row : questionRows) {
            Long qLevelId = questionLevelRepository.findByCode((row.questionLevel()));
            Question q = Question.of(row, qLevelId);
            result.add(q);
        }
        return result;
    }
}
