package com.wk.ti.resource.service;

import com.wk.ti.question.model.QuestionRow;
import com.wk.ti.question.model.Question;
import com.wk.ti.question.model.ResourceRequest;
import com.wk.ti.resource.model.Resource;
import com.wk.ti.resource.model.ResourceQuestion;
import com.wk.ti.resource.model.ResourceQuestionKey;
import com.wk.ti.resource.repository.ResourceQuestionRepository;
import com.wk.ti.resource.repository.ResourceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ResourceService {
    private final ResourceRepository resourceRepository;
    private final ResourceQuestionRepository resourceQuestionRepository;

    public void save(QuestionRow result, Question question) {
        if (result.resources() == null || result.resources().isEmpty()) {
            return;
        }
        List<ResourceQuestion> resourceQuestions = new ArrayList<>();
        for (ResourceRequest resourceDto : result.resources()) {
            Resource resource = Resource.builder()
                    .resourceUrl(resourceDto.url())
                    .description(resourceDto.description())
                    .build();
            Resource savedResource = resourceRepository.saveAndFlush(resource);
            ResourceQuestionKey key = ResourceQuestionKey.builder()
                    .questionId(question.getId())
                    .resourceId(savedResource.getId())
                    .build();
            ResourceQuestion rq = ResourceQuestion.builder()
                    .resourceQuestionKey(key)
                    .question(question)
                    .resource(savedResource)
                    .build();
            resourceQuestions.add(rq);
        }
        resourceQuestionRepository.saveAll(resourceQuestions);
    }
}
