package com.wk.ti.qlevel.service;

import com.wk.ti.qlevel.model.QuestionLevelDto;
import com.wk.ti.qlevel.repository.QuestionLevelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class QuestionLevelService {
    private final QuestionLevelRepository questionLevelRepository;

    public List<QuestionLevelDto> findAll() {
        return questionLevelRepository.findAll()
                .stream()
                .map(QuestionLevelDto::of)
                .toList();
    }
}
