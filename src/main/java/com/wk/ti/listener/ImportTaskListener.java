package com.wk.ti.listener;

import com.wk.ti.data.processor.DataParser;
import com.wk.ti.event.ImportCompletedEvent;
import com.wk.ti.event.FileProcessingEvent;
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

    private static final String EXCHANGE = "ti.import";
    private static final String RK_COMPLETED = "import.completed";
    private static final String RK_FAILED = "import.failed";

    @RabbitListener(queues = "import-worker.import")
    @Observed(name = "import.worker.process", contextualName = "process-import-file")
    public void processImport(FileProcessingEvent event) {
        log.info("Received processing request for jobId={}, path={}", event.jobId(), event.storedFilePath());

        File rawFile = new File(event.storedFilePath());
        if (!rawFile.exists()) {
            String errorMsg = "File not found at path: " + event.storedFilePath();
            handleFailure(event.jobId(), errorMsg, new FileNotFoundException(errorMsg));
            return;
        }
        try (InputStream inputStream = new FileInputStream(rawFile)) {

            byte[] file = inputStream.readAllBytes();
            List<QuestionRow> questionRows = dataParsers.stream()
                    .filter(dataParser -> dataParser.support(event.originalFilename()))
                    .findFirst()
                    .map(dataParser -> dataParser.parse(file, event.originalFilename()))
                    .orElseThrow(() -> new IllegalArgumentException("Unsupported file type: " + event.originalFilename()));

            log.info("Parsed {} questions from file {}", questionRows.size(), event.originalFilename());

            List<Question> questions = questionService.generate(questionRows);
            int inserted = questionService.bulkInsert(questions);

            log.info("Successfully imported {} questions from file {}", inserted, event.originalFilename());

            ImportCompletedEvent successEvent = new ImportCompletedEvent(event.jobId(), inserted);
            rabbitTemplate.convertAndSend(EXCHANGE, RK_COMPLETED, successEvent);

        } catch (Exception ex) {
            handleFailure(event.jobId(), ex.getMessage(), ex);
        } finally {
            // Clean up temporary local file storage
            if (rawFile.exists() && !rawFile.delete()) {
                log.warn("Failed to delete processed file: {}", rawFile.getAbsolutePath());
            }
        }
    }

    private void handleFailure(String jobId, String failureReason, Exception ex) {
        log.error("Import processing failed for jobId={}. Reason: {}", jobId, failureReason, ex);
        meterRegistry.counter("import.worker.failures", "exception", ex.getClass().getSimpleName()).increment();

        ImportFailedEvent failedEvent = new ImportFailedEvent(jobId, failureReason);
        rabbitTemplate.convertAndSend(EXCHANGE, RK_FAILED, failedEvent);
    }
}
