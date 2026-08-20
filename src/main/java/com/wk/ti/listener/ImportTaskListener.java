package com.wk.ti.listener;

import com.wk.ti.data.processor.DataParser;
import com.wk.ti.event.ImportCompletedEvent;
import com.wk.ti.event.ImportEvent;
import com.wk.ti.event.ImportFailedEvent;
import com.wk.ti.question.model.Question;
import com.wk.ti.question.model.QuestionRow;
import com.wk.ti.question.service.QuestionService;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.observation.annotation.Observed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ImportTaskListener {

    private final List<DataParser> dataParsers;
    private final QuestionService questionService;
    private final RabbitTemplate rabbitTemplate;
    private final MeterRegistry meterRegistry;

    private static final String EXCHANGE = "import.exchange";
    private static final String RK_COMPLETED = "import.completed";
    private static final String RK_FAILED = "import.failed";

    @RabbitListener(queues = "import-worker.import")
    @Observed(name = "import.worker.process", contextualName = "process-import-file")
    public void processImport(ImportEvent event) {
        log.info("Received processing request for importId={}, path={}", event.importId(), event.storedFilePath());

        File rawFile = new File(event.storedFilePath());
        if (!rawFile.exists()) {
            String errorMsg = "File not found at path: " + event.storedFilePath();
            handleFailure(event.importId(), errorMsg, new FileNotFoundException(errorMsg));
            return;
        }
        try (InputStream inputStream = new FileInputStream(rawFile)) {

            byte[] file = inputStream.readAllBytes();
            List<QuestionRow> questionRows = dataParsers.stream()
                    .filter(dataParser -> dataParser.support(event.originalFileName()))
                    .findFirst()
                    .map(dataParser -> dataParser.parse(file, event.originalFileName()))
                    .orElseThrow(() -> new IllegalArgumentException("Unsupported file type: " + event.originalFileName()));

            log.info("Parsed {} questions from file {}", questionRows.size(), event.originalFileName());

            List<Question> questions = questionService.generate(questionRows);
            int inserted = questionService.bulkInsert(questions);

            log.info("Successfully imported {} questions from file {}", inserted, event.originalFileName());

            ImportCompletedEvent successEvent = new ImportCompletedEvent(event.importId(), inserted);
            rabbitTemplate.convertAndSend(EXCHANGE, RK_COMPLETED, successEvent);

        } catch (Exception ex) {
            handleFailure(event.importId(), ex.getMessage(), ex);
        } finally {
            // Clean up temporary local file storage
            if (rawFile.exists() && !rawFile.delete()) {
                log.warn("Failed to delete processed file: {}", rawFile.getAbsolutePath());
            }
        }
    }

    private void handleFailure(String importId, String failureReason, Exception ex) {
        log.error("Import processing failed for importId={}. Reason: {}", importId, failureReason, ex);
        meterRegistry.counter("import.worker.failures", "exception", ex.getClass().getSimpleName()).increment();

        ImportFailedEvent failedEvent = new ImportFailedEvent(importId, failureReason);
        rabbitTemplate.convertAndSend(EXCHANGE, RK_FAILED, failedEvent);
    }
}
