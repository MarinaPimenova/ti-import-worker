package com.wk.ti.upload;

import com.wk.ti.data.processor.DataParser;
import com.wk.ti.question.model.Question;
import com.wk.ti.question.model.QuestionRow;
import com.wk.ti.question.service.QuestionService;

import com.wk.ti.upload.model.FileProcessingResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class TestService {

    private final List<DataParser> dataParsers;
    private final QuestionService questionService;

    public FileProcessingResponse upload(MultipartFile file) {
        // 1. Capture file metadata and content synchronously on the main thread
        String originalFilename = file.getOriginalFilename();

        byte[] fileBytes;
        try {
            fileBytes = file.getBytes();
        } catch (IOException e) {
            throw new IllegalStateException("Failed to read uploaded file", e);
        }

        // 2. Pass the byte array / filename to the virtual thread
        Thread.startVirtualThread(() -> {
            try {
                List<QuestionRow> questionRows = dataParsers.stream()
                        .filter(dataParser -> dataParser.support(originalFilename)) // Updated support method signature
                        .findFirst()
                        .map(dataParser -> dataParser.parse(fileBytes, originalFilename))
                        .orElseThrow(() -> new IllegalArgumentException("Unsupported file type: " + originalFilename));
                log.info("Parsed {} questions from file {}",
                        questionRows.size(),
                        originalFilename);

                List<Question> questions = questionService.generate(questionRows);

                int inserted = questionService.bulkInsert(questions);

                log.info("Imported {} questions from file {}",
                        inserted,
                        originalFilename);
            } catch (Exception e) {
                log.error("Async parsing failed for file {}", originalFilename, e);
            }
        });
        return new FileProcessingResponse(UUID.randomUUID().toString());
    }
}
