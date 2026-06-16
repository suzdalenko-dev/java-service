package net.javaservice.purchasing.excel;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

public class ExcelReader {

    public List<List<String>> readFirstSheet(Path filePath) throws IOException {
        return readSheet(filePath, 0);
    }

    public List<List<String>> readSheet(Path filePath, int sheetIndex) throws IOException {
        InputStream inputStream = Files.newInputStream(filePath);
        Workbook workbook = WorkbookFactory.create(inputStream);

        Sheet sheet = workbook.getSheetAt(sheetIndex);
        List<List<String>> rows = readRows(workbook.getCreationHelper().createFormulaEvaluator(), sheet);

        workbook.close();
        inputStream.close();

        return rows;
    }

    private List<List<String>> readRows(FormulaEvaluator evaluator, Sheet sheet) {
        List<List<String>> rows = new ArrayList<>();
        DataFormatter formatter = new DataFormatter(Locale.forLanguageTag("es-ES"));

        int maxColumns = getMaxColumns(sheet);

        for (int rowIndex = sheet.getFirstRowNum(); rowIndex <= sheet.getLastRowNum(); rowIndex++) {
            Row row = sheet.getRow(rowIndex);
            List<String> values = new ArrayList<>();

            for (int columnIndex = 0; columnIndex < maxColumns; columnIndex++) {
                String value = "";

                if (row != null) {
                    Cell cell = row.getCell(columnIndex, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);

                    if (cell != null) {
                        value = formatter.formatCellValue(cell, evaluator).trim();
                    }
                }

                values.add(value);
            }

            rows.add(values);
        }

        return rows;
    }

    private int getMaxColumns(Sheet sheet) {
        int maxColumns = 0;

        for (int rowIndex = sheet.getFirstRowNum(); rowIndex <= sheet.getLastRowNum(); rowIndex++) {
            Row row = sheet.getRow(rowIndex);

            if (row != null && row.getLastCellNum() > maxColumns) {
                maxColumns = row.getLastCellNum();
            }
        }

        return maxColumns;
    }
}