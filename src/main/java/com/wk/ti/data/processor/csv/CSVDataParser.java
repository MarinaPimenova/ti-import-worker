package com.wk.ti.data.processor.csv;

import com.wk.ti.data.processor.DataParser;
import com.wk.ti.question.model.QuestionRow;
import com.wk.ti.question.model.ResourceRequest;
import jakarta.validation.constraints.NotNull;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.input.BOMInputStream;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;

@SuppressWarnings("deprecation")
@Service
public class CSVDataParser implements DataParser {

    private static final List<String> EXPECTED_HEADERS = List.of(
            "QUESTION",
            "LEVEL",
            "SHORT ANSWER",
            "RESOURCE"
    );

    private static final CSVFormat CSV_FORMAT = CSVFormat.DEFAULT.builder()
            .setHeader()
            .setSkipHeaderRecord(true)
            .setIgnoreEmptyLines(true)
            .setTrim(true)
            .build();

    @Override
    public boolean support(@NotNull String filename) {
        return filename != null
                && filename.toLowerCase().endsWith(".csv");
    }

    @Override
    public List<QuestionRow> parse(byte[] bytes, String filename) {
        try (InputStream inputStream = new ByteArrayInputStream(bytes);
             BOMInputStream bomInputStream = BOMInputStream.builder()
                     .setInputStream(inputStream)
                     .get();
             Reader reader = new InputStreamReader(
                     bomInputStream,
                     StandardCharsets.UTF_8);
             CSVParser parser = CSV_FORMAT.parse(reader)) {

            validateHeader(parser);

            return parser.stream()
                    .map(this::toQuestionRow)
                    .toList();

        } catch (IOException e) {
            throw new IllegalStateException(
                    "Failed to parse CSV file: "
                            + filename,
                    e
            );
        }
    }

    private void validateHeader(CSVParser parser) {
        List<String> headers = parser.getHeaderNames();

        List<String> normalizedHeaders = headers.stream()
                .map(header -> header.replace("\uFEFF", ""))
                .map(String::trim)
                .map(String::toUpperCase)
                .toList();

        if (!EXPECTED_HEADERS.equals(normalizedHeaders)) {
            throw new IllegalArgumentException(
                    "Invalid CSV header. Expected: "
                            + String.join(",", EXPECTED_HEADERS)
                            + ", but found: "
                            + String.join(",", headers)
            );
        }
    }

    private QuestionRow toQuestionRow(CSVRecord record) {
        String resource = record.get("RESOURCE");

        List<ResourceRequest> resources =
                resource.isBlank()
                        ? List.of()
                        : List.of(getResource(resource));

        return new QuestionRow(
                record.get("QUESTION"),
                record.get("SHORT ANSWER"),
                record.get("LEVEL"),
                resources
        );
    }
}