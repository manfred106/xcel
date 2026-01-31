package com.lumion.xcel.validation.poi;

import com.lumion.xcel.validation.core.CoreValidator;
import com.lumion.xcel.validation.core.ValidationError;
import com.lumion.xcel.validation.core.config.FieldConfig;
import com.lumion.xcel.validation.core.config.ValidationConfig;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PoiValidator {
    private final CoreValidator coreValidator;

    public PoiValidator() {
        this(new CoreValidator());
    }

    public PoiValidator(CoreValidator coreValidator) {
        this.coreValidator = coreValidator;
    }

    public List<ValidationError> validate(Sheet sheet, ValidationConfig config) {
        if (sheet == null || config == null || config.getFields() == null || config.getFields().isEmpty()) {
            return Collections.emptyList();
        }

        var session = coreValidator.startSession(config);
        Map<String, Integer> headerIndex = readHeader(sheet);
        if (headerIndex.isEmpty()) {
            return Collections.emptyList();
        }

        List<ValidationError> errors = new ArrayList<>();

        int lastRow = sheet.getLastRowNum();
        for (int i = 1; i <= lastRow; i++) {
            Row row = sheet.getRow(i);
            Map<String, Object> rowData = new HashMap<>();
            boolean hasValue = false;

            for (FieldConfig field : config.getFields()) {
                if (field.getFieldName() == null || field.getFieldName().isBlank()) {
                    continue;
                }
                Integer columnIndex = headerIndex.get(field.getFieldName());
                if (columnIndex == null) {
                    continue;
                }

                Object value = readCellValue(row, columnIndex);
                rowData.put(field.getFieldName(), value);
                if (!isBlank(value)) {
                    hasValue = true;
                }
            }

            if (hasValue) {
                errors.addAll(session.validateRow(rowData, i));
            }
        }

        return errors;
    }

    private Map<String, Integer> readHeader(Sheet sheet) {
        Map<String, Integer> headerIndex = new HashMap<>();
        Row headerRow = sheet.getRow(0);
        if (headerRow == null) {
            return headerIndex;
        }

        short lastCellNum = headerRow.getLastCellNum();
        for (int i = 0; i < lastCellNum; i++) {
            Cell cell = headerRow.getCell(i);
            if (cell == null) {
                continue;
            }
            String header = cell.toString().trim();
            if (!header.isBlank()) {
                headerIndex.put(header, i);
            }
        }

        return headerIndex;
    }

    private Object readCellValue(Row row, int columnIndex) {
        if (row == null) {
            return null;
        }
        Cell cell = row.getCell(columnIndex);
        if (cell == null) {
            return null;
        }

        CellType cellType = cell.getCellType();
        if (cellType == CellType.FORMULA) {
            cellType = cell.getCachedFormulaResultType();
        }

        switch (cellType) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    LocalDate date = cell.getLocalDateTimeCellValue().toLocalDate();
                    return date;
                }
                return cell.getNumericCellValue();
            case BOOLEAN:
                return cell.getBooleanCellValue();
            case BLANK:
            case ERROR:
            default:
                return null;
        }
    }

    private boolean isBlank(Object value) {
        if (value == null) {
            return true;
        }
        if (value instanceof String) {
            return ((String) value).trim().isEmpty();
        }
        return false;
    }

 
}
