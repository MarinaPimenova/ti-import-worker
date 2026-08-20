package com.wk.ti.data.processor;

import com.wk.ti.question.model.QuestionRow;
import com.wk.ti.question.model.ResourceRequest;

import java.util.List;

public interface DataParser {
    boolean support(String filename);
    List<QuestionRow> parse(byte[] bytes, String filename);

    default ResourceRequest getResource(String resource) {
        if (resource == null) {
            return new ResourceRequest("", "");
        }
        return resource.contains("http") ? new ResourceRequest(resource, null)
                : new ResourceRequest(null, resource);
    }
}
