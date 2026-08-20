package com.wk.ti.data.processor.excel;

import com.wk.ti.data.processor.DataParser;
import com.wk.ti.question.model.QuestionRow;
import com.wk.ti.question.model.ResourceRequest;
import jakarta.validation.constraints.NotNull;
import org.dhatim.fastexcel.reader.ReadableWorkbook;
import org.dhatim.fastexcel.reader.Row;
import org.dhatim.fastexcel.reader.Sheet;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
public class ExcelDataParser implements DataParser {

    private static final int QUESTION = 0;
    private static final int LEVEL = 1;
    private static final int SHORT_ANSWER = 2;
    private static final int RESOURCE = 3;

    private static final List<String> EXPECTED_HEADERS = List.of(
            "QUESTION",
            "LEVEL",
            "SHORT ANSWER",
            "RESOURCE"
    );

    @Override
    public boolean support(@NotNull String filename) {
        if (filename == null) {
            return false;
        }

        return filename.toLowerCase(Locale.ROOT).endsWith(".xlsx");
    }

    @Override
    public List<QuestionRow> parse(byte[] bytes, String filename) {

        try (InputStream inputStream = new ByteArrayInputStream(bytes);
                ReadableWorkbook workbook =
                     new ReadableWorkbook(inputStream)) {

            Sheet sheet = workbook.getFirstSheet();

            List<QuestionRow> result = new ArrayList<>();

            try (var rows = sheet.openStream()) {

                boolean header = true;

                for (Row row : (Iterable<Row>) rows::iterator) {

                    if (header) {
                        validateHeader(row);
                        header = false;
                        continue;
                    }

                    if (isEmpty(row)) {
                        continue;
                    }

                    result.add(toQuestionRow(row));
                }
            }

            return result;

        } catch (IOException e) {
            throw new IllegalStateException(
                    "Failed to parse Excel file: "
                            + filename, e);
        }
    }

    private void validateHeader(Row row) {

        for (int column = 0; column < EXPECTED_HEADERS.size(); column++) {

            String actual = getCellValue(row, column);

            if (!EXPECTED_HEADERS.get(column)
                    .equalsIgnoreCase(actual.trim())) {

                throw new IllegalArgumentException(
                        "Invalid Excel header. Expected column "
                                + column
                                + " to be '"
                                + EXPECTED_HEADERS.get(column)
                                + "' but was '"
                                + actual
                                + "'");
            }
        }
    }

    private QuestionRow toQuestionRow(Row row) {

        String resource = getCellValue(row, RESOURCE);

        List<ResourceRequest> resources =
                resource.isBlank()
                        ? List.of()
                        : List.of(getResource(resource));

        return new QuestionRow(
                getCellValue(row, QUESTION),
                getCellValue(row, SHORT_ANSWER),
                getCellValue(row, LEVEL),
                resources
        );
    }

    private String getCellValue(Row row, int column) {
        return row.getCellAsString(column)
                .orElse("")
                .trim();
    }

    private boolean isEmpty(Row row) {
        return getCellValue(row, QUESTION).isBlank()
                && getCellValue(row, LEVEL).isBlank()
                && getCellValue(row, SHORT_ANSWER).isBlank()
                && getCellValue(row, RESOURCE).isBlank();
    }
}